# B2B Chat Agent (Python)

LangGraph agent that connects to the Spring Boot MCP server and provides a chat API for the Angular frontend.

## Setup

1. Install dependencies:
   ```bash
   pip install -r requirements.txt
   ```

2. Copy `.env.example` to `.env` and set your `GROQ_API_KEY`:
   ```bash
   cp .env.example .env
   ```

3. Ensure the Spring Boot backend is running on port 8080.

## Run

```bash
python server.py
```

The FastAPI server starts on `http://localhost:8000`.

## API

- `GET /health` - Health check
- `POST /chat` - Send a chat message. Body: `{"message": "..."}`

## Architecture

Angular Frontend (port 4200)
  → HTTP POST /chat
Python FastAPI Server (port 8000)
  → SSE MCP Client
Spring Boot MCP Server (port 8080)
  → Database
