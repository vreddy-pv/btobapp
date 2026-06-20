import os
from dotenv import load_dotenv

load_dotenv()

MCP_SERVER_URL = os.getenv("MCP_SERVER_URL", "http://localhost:8080")

SYSTEM_PROMPT = """You are a helpful B2B auto parts assistant. You help customers check order statuses and create new orders.

Available tools:
- check_order_status(orderId: str): Check the status of an existing order. Returns order details including status, account, total amount, date, and line items.
- create_b2b_order(accountId: str, items: list[dict]): Create a new order. Each item must have 'sku' (string) and 'quantity' (integer).

When a user asks about an order, extract the order ID (e.g., ORD-001) and call check_order_status.
When a user wants to place an order, extract the account ID (e.g., ACC-001) and items, then call create_b2b_order.

Available accounts: ACC-001 (Downtown Auto Repair), ACC-002 (Quick Fix Garage), ACC-003 (Premier Motors).
Available parts: BRK-001, BRK-002, FLT-001, FLT-002, ENG-001, SUS-001, ELT-001, BRT-001.

Be concise and professional."""


class AgentManager:
    def __init__(self):
        self.mcp_client = None
        self.agent = None

    async def initialize(self):
        from langchain_mcp_adapters.client import MultiServerMCPClient
        from langgraph.prebuilt import create_react_agent
        from langchain_groq import ChatGroq

        self.mcp_client = MultiServerMCPClient(
            {
                "btob-mcp-server": {
                    "url": f"{MCP_SERVER_URL}/sse",
                    "transport": "sse",
                },
            }
        )

        tools = await self.mcp_client.get_tools()

        llm = ChatGroq(
            model="llama-3.3-70b-versatile",
            temperature=0.1,
        )

        self.agent = create_react_agent(
            llm,
            tools,
            prompt=SYSTEM_PROMPT,
        )

    async def close(self):
        self.mcp_client = None
        self.agent = None

    async def process_message(self, message: str) -> str:
        result = await self.agent.ainvoke(
            {"messages": [("user", message)]}
        )
        return result["messages"][-1].content
