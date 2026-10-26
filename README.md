# 🛍️ AI Chatbot with RAG

> Tính năng Chatbot tra cứu sản phẩmứng dụng kiến trúc **RAG (Retrieval-Augmented Generation)** xây dựng trên nền tảng **Java 23**, **Spring Boot**, **Spring AI**, **Google Gemini** và **ChromaDB**.

---

## 🔄 Kiến trúc & Flowchart 2 luồng API cốt lõi

### Luồng 1: API Thêm sản phẩm & Tự động nạp Vector bất đồng bộ (`POST /api/products`)

Khi admin thêm sản phẩm mới vào hệ thống, dữ liệu được lưu tức thì vào MySQL và kích hoạt luồng `@Async` để trích xuất embedding nạp vào ChromaDB mà không làm block API.

```mermaid
flowchart TD
    A([Client: POST /api/products]) --> B[ProductController]
    B --> C[ProductService: createProduct]
    
    subgraph Synchronous Flow ["Luồng đồng bộ (Fast Response)"]
        C --> D[(MySQL: Lưu Product)]
        D --> E([Trả về HTTP 201 Created])
    end
    
    subgraph Asynchronous Pipeline ["Luồng bất đồng bộ (@Async Task)"]
        C -.->|Trigger Async| F[ProductVectorService: saveProductVector]
        F --> G[Build Document Text & Metadata]
        G --> H[TokenTextSplitter: Chunking Text]
        H --> I[Google Gemini: Sinh Vector Embedding]
        I --> J[(ChromaDB: Lưu Vector & Metadata)]
    end

    classDef sync fill:#e1f5fe,stroke:#03a9f4,stroke-width:2px;
    classDef async fill:#fff3e0,stroke:#ff9800,stroke-width:2px;
    class D,E sync;
    class F,G,H,I,J async;
```

---

### Luồng 2: API Chat & Tra cứu sản phẩm thông minh bằng RAG (`POST /api/chat`)

Khi khách hàng gửi câu hỏi tư vấn, hệ thống tìm kiếm các sản phẩm tương đồng nhất trong ChromaDB, ghép vào Prompt có quy tắc kiểm duyệt và gửi đến Google Gemini để sinh câu trả lời.

```mermaid
flowchart TD
    U([User: POST /api/chat]) --> CC[ChatController]
    CC --> CS[ChatService: chat]
    
    subgraph Vector Retrieval ["1. Tìm kiếm ngữ cảnh (Retrieval)"]
        CS -->|Search query| VS[(ChromaDB)]
        VS -->|Top-K Similar Documents + Metadata| CS
    end
    
    subgraph Prompt Augmentation ["2. Ghép ngữ cảnh & Áp quy tắc (Augmentation)"]
        CS --> PT["Prompt Template Ràng buộc:
        - Chỉ dùng thông tin từ kho
        - Tuyệt đối không bịa đặt
        - Trả lời 'không xác định' nếu thiếu dữ liệu"]
    end
    
    subgraph Generation ["3. Sinh câu trả lời (Generation)"]
        PT --> LLM[Google Gemini LLM]
        LLM --> RESP[Tạo câu trả lời tự nhiên & chính xác]
        RESP --> RET([Trả về ChatResponse])
    end

    classDef ret fill:#e8f5e9,stroke:#4caf50,stroke-width:2px;
    classDef gen fill:#f3e5f5,stroke:#9c27b0,stroke-width:2px;
    class VS,CS ret;
    class PT,LLM,RESP,RET gen;
```

---

## 📡 Danh sách API chính (API Endpoints)

### 1. Thêm sản phẩm mới
* **Endpoint:** `POST /api/products`
* **Request Body:**
```json
{
  "name": "Bàn phím cơ không dây MonsGeek M1W",
  "price": 2350000,
  "description": "Bàn phím nhôm CNC, 3 mode kết nối, mạch xuôi, gasket mount",
  "stockQuantity": 20
}
```
* **Response (HTTP 201):**
```json
{
  "success": true,
  "message": "Tạo sản phẩm thành công",
  "data": {
    "id": 6,
    "name": "Bàn phím cơ không dây MonsGeek M1W",
    "price": 2350000,
    "stockQuantity": 20
  }
}
```

---

### 2. Chat / Tra cứu sản phẩm (RAG)
* **Endpoint:** `POST /api/chat`
* **Request Body:**
```json
{
  "message": "Tôi đang tìm một chiếc bàn phím cơ kết nối không dây, giá tầm hơn 1 triệu chút"
}
```
* **Response (HTTP 200):**
```json
{
  "success": true,
  "message": "Trả lời thành công",
  "data": {
    "answer": "Chào bạn, cửa hàng có sản phẩm Bàn phím cơ không dây RK84 với giá 1.250.000 VNĐ. Sản phẩm hỗ trợ 3 mode kết nối (Bluetooth/2.4G/Type-C) và có tính năng Hotswap RGB, hiện còn 50 chiếc trong kho."
  }
}
```
