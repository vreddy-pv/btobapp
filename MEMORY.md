# B2B Auto Parts Application - Memory

## Project Overview
B2B Auto Parts e-commerce application with Spring Boot backend and Angular 21 frontend with Tailwind CSS v4. Features an AI-powered chat assistant using MCP (Model Context Protocol).

## Tech Stack
- **Backend**: Spring Boot 3.2.4, Java 17, H2 in-memory DB, JPA/Hibernate
- **Frontend**: Angular 21.2, Tailwind CSS v4 (CDN), Angular Signals
- **AI Integration**: Custom MCP server (JSON-RPC 2.0 over SSE)
- **Ports**: Backend 8080, Frontend 4200

## Architecture
- **Monorepo**: Backend and frontend in one repository
- **4 Entities**: B2BAccount, AutoPart, SalesOrder, OrderLineItem
- **3 REST Controllers**: AccountController, CatalogController, OrderController
- **MCP Layer**: McpController, McpToolRegistry, McpSessionManager, 2 tool implementations
- **Frontend Pages**: Dashboard, Catalog, Orders, Accounts, Chat widget

## Key Files
### Backend
- `src/main/java/com/btob/app/` - All Java source
- `src/main/resources/application.yml` - Server config (port 8080, H2 DB)
- `src/main/resources/data.sql` - Seed data (3 accounts, 15 parts, 3 orders)
- `src/main/java/com/btob/app/mcp/` - MCP protocol implementation
- `src/main/java/com/btob/app/mcp/handler/` - CheckOrderStatusTool, CreateB2BOrderTool
- `src/main/java/com/btob/app/config/CorsConfig.java` - CORS for /api/** and /mcp/**

### Frontend
- `frontend/src/app/app.ts` - Root component with shared sidebar
- `frontend/src/app/app.routes.ts` - Routes: /, /catalog, /orders, /accounts
- `frontend/src/app/dashboard/` - Dashboard page (KPIs, parts grid, orders table)
- `frontend/src/app/pages/catalog/` - Catalog page
- `frontend/src/app/pages/orders/` - Orders page
- `frontend/src/app/pages/accounts/` - Accounts page
- `frontend/src/app/chat/` - Floating chat widget
- `frontend/src/app/services/api.service.ts` - REST API client
- `frontend/src/app/services/mcp-client.service.ts` - MCP SSE/JSON-RPC client
- `frontend/src/app/services/agent.service.ts` - Intent classification + tool orchestration
- `frontend/src/app/models/api.ts` - TypeScript interfaces
- `frontend/src/index.html` - Tailwind CDN script tag
- `frontend/src/styles.css` - Custom animations + SVG fallback

## Issues Fixed
1. **Tailwind CSS not loading** - Angular's esbuild builder doesn't process PostCSS config. Fixed by adding Tailwind CDN script to index.html
2. **SVG icons rendering huge** - Added explicit width/height attributes to all SVG elements
3. **Sidebar navigation not working** - Sidebar was static `<a>` tags with no routerLink. Created dedicated page components (Catalog, Orders, Accounts) with routing
4. **Duplicate sidebars** - Moved sidebar to shared app.html layout, removed from dashboard.html
5. **Chat bot not classifying intents correctly** - "order" matched both check and create regex patterns. Fixed by prioritizing order ID detection and fixing priority logic
6. **CORS blocking MCP** - CorsConfig only allowed /api/**. Added /mcp/** mapping
7. **All products showing same icon** - Added category-specific SVG icons and color schemes

## Design Decisions
- Tailwind v4 via CDN (not PostCSS) because Angular's esbuild doesn't support PostCSS config
- Explicit SVG width/height attributes for reliable sizing
- Shared sidebar in app.html (not duplicated per page)
- Rule-based intent classification in AgentService (no LLM dependency)
- Custom MCP implementation (no third-party library)
- H2 in-memory DB with create-drop (data resets on restart)

## Seed Data
- **Accounts**: ACC-001 (Downtown Auto Repair, GOLD), ACC-002 (Quick Fix Garage, SILVER), ACC-003 (Premier Motors, GOLD)
- **Parts**: 15 items across 6 categories (Brakes, Filters, Engine, Suspension, Electrical, Belts)
- **Orders**: ORD-001 (DELIVERED), ORD-002 (SHIPPED), ORD-003 (PENDING)
