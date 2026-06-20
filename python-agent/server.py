import os
import traceback
from contextlib import asynccontextmanager
from dotenv import load_dotenv
from fastapi import FastAPI, HTTPException
from fastapi.middleware.cors import CORSMiddleware
from pydantic import BaseModel

load_dotenv()

HOST = os.getenv("HOST", "0.0.0.0")
PORT = int(os.getenv("PORT", "8000"))
FRONTEND_URL = os.getenv("FRONTEND_URL", "http://localhost:4200")


class ChatRequest(BaseModel):
    message: str


class ChatResponse(BaseModel):
    response: str


manager = None


@asynccontextmanager
async def lifespan(app: FastAPI):
    from agent import AgentManager
    global manager
    manager = AgentManager()
    await manager.initialize()
    yield
    if manager:
        await manager.close()
        manager = None


app = FastAPI(title="B2B Chat Agent", lifespan=lifespan)

app.add_middleware(
    CORSMiddleware,
    allow_origins=[FRONTEND_URL],
    allow_methods=["*"],
    allow_headers=["*"],
)


@app.get("/health")
async def health():
    return {"status": "ok", "agent": manager is not None and manager.agent is not None}


@app.post("/chat", response_model=ChatResponse)
async def chat(request: ChatRequest):
    if manager is None or manager.agent is None:
        raise HTTPException(status_code=503, detail="Agent not initialized")

    try:
        response_text = await manager.process_message(request.message)
        return ChatResponse(response=response_text)

    except Exception as e:
        traceback.print_exc()
        raise HTTPException(status_code=500, detail=str(e))


def main():
    import uvicorn
    uvicorn.run("server:app", host=HOST, port=PORT, reload=True)


if __name__ == "__main__":
    main()
