# 文档摘要与知识库系统 — 后端架构文档

> 生成日期：2026-06-23  
> 项目：doc-summary-kb-backend  
> 基准路径：`backend/src/main/java/com/example/dockb/`

---

## 一、技术栈

| 组件 | 选型 | 版本 |
|------|------|------|
| 框架 | Spring Boot | 3.2.5 |
| ORM | MyBatis-Plus | 3.5.5 |
| 数据库 | MySQL | 8.0+ |
| 安全 | JJWT + BCrypt | 0.12.5 |
| 工具库 | Hutool | 5.8.27 |
| 简化代码 | Lombok | 1.18.30 |
| PDF 解析 | PDFBox | 2.0.30 |
| Word 解析 | Apache POI | 5.2.5 |
| 流式推送 | Spring WebFlux | (与 Boot 同版) |
| Java | JDK | 17 |

---

## 二、目录结构总览

```
backend/src/main/
├── java/com/example/dockb/
│   ├── DocKbApplication.java          # Spring Boot 启动类
│   ├── annotation/
│   │   └── RequireRole.java           # 角色权限注解
│   ├── client/
│   │   ├── DefaultM3Client.java       # AI HTTP 客户端实现
│   │   ├── M3Client.java              # AI 客户端抽象接口
│   │   ├── M3Exception.java           # AI 调用异常
│   │   └── dto/
│   │       ├── ChatRequest.java       # 通用 Chat 请求 DTO
│   │       ├── ChatResponse.java      # 通用 Chat 响应 DTO
│   │       ├── QaResult.java          # 问答结果封装
│   │       └── RankedHit.java         # 重排结果
│   ├── common/
│   │   ├── BizException.java          # 业务异常
│   │   ├── GlobalExceptionHandler.java# 全局异常处理器
│   │   ├── PageResult.java            # 分页响应封装
│   │   ├── Result.java                # 统一响应体
│   │   └── ResultCode.java            # 状态码常量
│   ├── config/
│   │   ├── ai/
│   │   │   └── ModelRegistry.java     # AI 模型注册表
│   │   ├── AppProperties.java         # 应用级配置绑定
│   │   ├── AsyncConfig.java           # 异步线程池
│   │   ├── CorsConfig.java            # 跨域配置
│   │   ├── CurrentUser.java           # @CurrentUser 注解
│   │   ├── CurrentUserArgumentResolver.java
│   │   ├── DeepSeekProperties.java    # DeepSeek API 配置
│   │   ├── JwtAuthFilter.java         # JWT 认证过滤器
│   │   ├── M3Properties.java          # MiniMax API 配置
│   │   ├── MybatisAutoFillHandler.java# 自动填充处理器
│   │   ├── MybatisPlusConfig.java     # 分页插件配置
│   │   ├── RestClientConfig.java      # HTTP 客户端 Bean
│   │   └── WebConfig.java             # MVC 配置
│   ├── controller/
│   │   ├── AuthController.java        # 认证接口
│   │   ├── DocumentController.java    # 文档管理接口
│   │   ├── HealthController.java      # 健康检查接口
│   │   ├── ModelController.java       # 模型管理接口
│   │   ├── QaController.java          # 智能问答接口
│   │   └── UserController.java        # 用户管理接口
│   ├── dto/
│   │   ├── ChangePasswordRequest.java
│   │   ├── EvaluateRequest.java
│   │   ├── LoginRequest.java
│   │   ├── QaAskRequest.java
│   │   └── RegisterRequest.java
│   ├── entity/
│   │   ├── Document.java
│   │   ├── DocumentChunk.java
│   │   ├── QaHistory.java
│   │   └── User.java
│   ├── interceptor/
│   │   └── AuthorizationInterceptor.java
│   ├── mapper/
│   │   ├── DocumentChunkMapper.java
│   │   ├── DocumentMapper.java
│   │   ├── QaHistoryMapper.java
│   │   └── UserMapper.java
│   ├── service/
│   │   ├── AuthService.java
│   │   ├── DocumentService.java
│   │   ├── M3Service.java
│   │   ├── QaService.java
│   │   └── impl/
│   │       ├── AuthServiceImpl.java
│   │       ├── DocumentServiceImpl.java
│   │       ├── M3ServiceImpl.java
│   │       └── QaServiceImpl.java
│   ├── util/
│   │   ├── AuthContext.java           # 当前用户上下文 (ThreadLocal)
│   │   ├── JwtUtil.java               # JWT 工具
│   │   ├── SnippetUtil.java           # 检索片段高亮
│   │   ├── TextChunker.java           # 文本分块器
│   │   └── TextExtractor.java         # 文件文本提取器
│   └── vo/
│       ├── AuthVO.java
│       ├── CitationVO.java
│       ├── DocumentVO.java
│       ├── HealthVO.java
│       ├── ModelVO.java
│       ├── QaAnswerVO.java
│       ├── QaHistoryVO.java
│       ├── SearchHitVO.java
│       ├── SearchResponseVO.java
│       └── UserVO.java
└── resources/
    ├── application.yml                # 主配置文件
    └── db/
        ├── schema.sql                 # 建表脚本（幂等）
        └── data-seed.sql              # 种子数据
```

---

## 三、功能模块详解

### 模块 1：认证与用户管理

#### 3.1.1 涉及文件

| 层次 | 文件 | 路径 |
|------|------|------|
| Controller | `AuthController.java` | `.../controller/AuthController.java` |
| Controller | `UserController.java` | `.../controller/UserController.java` |
| Service | `AuthService.java` | `.../service/AuthService.java` |
| Service Impl | `AuthServiceImpl.java` | `.../service/impl/AuthServiceImpl.java` |
| Entity | `User.java` | `.../entity/User.java` |
| Mapper | `UserMapper.java` | `.../mapper/UserMapper.java` |
| DTO | `LoginRequest.java` | `.../dto/LoginRequest.java` |
| DTO | `RegisterRequest.java` | `.../dto/RegisterRequest.java` |
| DTO | `ChangePasswordRequest.java` | `.../dto/ChangePasswordRequest.java` |
| VO | `AuthVO.java` | `.../vo/AuthVO.java` |
| VO | `UserVO.java` | `.../vo/UserVO.java` |
| 拦截器 | `AuthorizationInterceptor.java` | `.../interceptor/AuthorizationInterceptor.java` |
| 注解 | `RequireRole.java` | `.../annotation/RequireRole.java` |
| 工具 | `JwtUtil.java` | `.../util/JwtUtil.java` |
| 工具 | `AuthContext.java` | `.../util/AuthContext.java` |
| 配置 | `JwtAuthFilter.java` | `.../config/JwtAuthFilter.java` |
| 配置 | `CurrentUser.java` | `.../config/CurrentUser.java` |
| 配置 | `CurrentUserArgumentResolver.java` | `.../config/CurrentUserArgumentResolver.java` |

#### 3.1.2 API 接口

| 方法 | 路径 | 说明 | 权限 |
|------|------|------|------|
| POST | `/api/auth/register` | 用户注册 | 公开 |
| POST | `/api/auth/login` | 用户登录 | 公开 |
| PUT | `/api/auth/password` | 修改密码 | 登录用户 |
| GET | `/api/admin/users` | 用户列表 | ADMIN |
| PUT | `/api/admin/users/{id}/role` | 切换角色 | ADMIN |

#### 3.1.3 认证流程

```
请求 Header: Authorization: Bearer <JWT>
    │
    ▼
JwtAuthFilter (OncePerRequestFilter)
    ├── 提取 Token
    ├── JwtUtil 校验签名 + 有效期
    ├── 解析出 userId / username / role
    └── 写入 AuthContext (ThreadLocal)
            │
            ▼
Controller 通过 @CurrentUser 注解注入当前用户
    └── AuthorizationInterceptor 检查 @RequireRole 注解
```

- JWT 密钥在 `application.yml` 的 `app.jwt.secret` 配置，过期时间 7 天
- 密码使用 BCrypt 加密存储
- 默认种子用户：`admin` / `Admin@123456` (ADMIN)、`test` / `Test@123456` (USER)

---

### 模块 2：文档管理

#### 3.2.1 涉及文件

| 层次 | 文件 | 路径 |
|------|------|------|
| Controller | `DocumentController.java` | `.../controller/DocumentController.java` |
| Service | `DocumentService.java` | `.../service/DocumentService.java` |
| Service Impl | `DocumentServiceImpl.java` | `.../service/impl/DocumentServiceImpl.java` |
| Entity | `Document.java` | `.../entity/Document.java` |
| Entity | `DocumentChunk.java` | `.../entity/DocumentChunk.java` |
| Mapper | `DocumentMapper.java` | `.../mapper/DocumentMapper.java` |
| Mapper | `DocumentChunkMapper.java` | `.../mapper/DocumentChunkMapper.java` |
| VO | `DocumentVO.java` | `.../vo/DocumentVO.java` |
| VO | `SearchHitVO.java` | `.../vo/SearchHitVO.java` |
| VO | `SearchResponseVO.java` | `.../vo/SearchResponseVO.java` |
| 工具 | `TextExtractor.java` | `.../util/TextExtractor.java` |
| 工具 | `TextChunker.java` | `.../util/TextChunker.java` |
| 工具 | `SnippetUtil.java` | `.../util/SnippetUtil.java` |

#### 3.2.2 API 接口

| 方法 | 路径 | 说明 | 权限 |
|------|------|------|------|
| POST | `/api/documents/upload` | 上传文档 | 登录用户 |
| GET | `/api/documents` | 文档分页列表 | 权限感知 |
| GET | `/api/documents/{id}` | 文档详情 | 权限感知 |
| GET | `/api/documents/{id}/file` | 在线预览原文件 | 权限感知 |
| DELETE | `/api/documents/{id}` | 删除文档 | 上传者或管理员 |
| GET | `/api/documents/categories` | 分类聚合列表 | 权限感知 |
| GET | `/api/documents/search` | 全文检索 | 权限感知 |

#### 3.2.3 上传处理链路

```
1. 接收 MultipartFile
     │
     ▼
2. 保存文件到 uploads/ 目录
     │
     ▼
3. 写入 document 表 (status=pending)
     │
     ▼
4. TextExtractor 提取全文
     ├── PDF  → PDFBox
     ├── DOCX → Apache POI
     └── TXT/MD → 直接读取
     │
     ▼
5. TextChunker 按自然段+字符数阈值分段 (默认 chunk-size=1000)
     │
     ▼
6. 批量写入 document_chunk 表
     │
     ▼
7. @Async 异步调用 AI (不阻塞上传响应)
     ├── classifyWithFallback()  → 更新 category
     ├── summarizeWithFallback() → 更新 summary
     └── extractTagsWithFallback() → 更新 tags
     失败仅设 status=failed，不影响文档可用性
```

#### 3.2.4 权限模型

| 角色 | 可见文档范围 |
|------|------------|
| ADMIN | 所有文档 |
| USER (登录) | 公开文档 (`owner_id IS NULL`) + 自己的文档 |
| 匿名 | 仅公开文档 |

#### 3.2.5 支持的文件格式

| 格式 | 解析库 | 说明 |
|------|--------|------|
| PDF | PDFBox 2.0.30 | 全文提取 |
| DOCX | Apache POI 5.2.5 | 含段落解析 |
| TXT | Java IO | 直接读取 |
| MD | Java IO | Markdown 文本 |

---

### 模块 3：智能问答

#### 3.3.1 涉及文件

| 层次 | 文件 | 路径 |
|------|------|------|
| Controller | `QaController.java` | `.../controller/QaController.java` |
| Service | `QaService.java` | `.../service/QaService.java` |
| Service Impl | `QaServiceImpl.java` | `.../service/impl/QaServiceImpl.java` |
| Entity | `QaHistory.java` | `.../entity/QaHistory.java` |
| Mapper | `QaHistoryMapper.java` | `.../mapper/QaHistoryMapper.java` |
| DTO | `QaAskRequest.java` | `.../dto/QaAskRequest.java` |
| DTO | `EvaluateRequest.java` | `.../dto/EvaluateRequest.java` |
| VO | `QaAnswerVO.java` | `.../vo/QaAnswerVO.java` |
| VO | `QaHistoryVO.java` | `.../vo/QaHistoryVO.java` |
| VO | `CitationVO.java` | `.../vo/CitationVO.java` |

#### 3.3.2 API 接口

| 方法 | 路径 | 说明 | 权限 |
|------|------|------|------|
| POST | `/api/qa/ask` | 同步问答 | 公开 |
| POST | `/api/qa/ask/stream` | 流式问答 (SSE) | 公开 |
| GET | `/api/qa/history` | 问答历史分页 | 登录用户 |
| POST | `/api/qa/evaluate/{id}` | 评测问答记录 | 登录用户 |
| GET | `/api/qa/evaluation-stats` | 评测统计数据 | 公开 |

#### 3.3.3 问答流程

```
用户提问 (question)
     │
     ▼
关键词检索 document_chunk 表
     ├── 权限过滤（同文档模块的权限模型）
     └── 取 topK 个最相关片段
     │
     ▼
拼接上下文 Prompt:
    "基于以下文档片段回答问题：
     【片段1】... 【片段2】... 
     问题：{question}"
     │
     ▼
调用 M3Service.answerWithFallback()
     ├── 同步模式：返回完整 QaAnswerVO (answer + citations)
     └── 流式模式：SSE 逐 token 推送 (text/event-stream)
     │
     ▼
异步保存到 qa_history 表
     ├── question / answer / citations (JSON)
     └── owner_id (可为 null)
```

#### 3.3.4 评测体系

| 字段 | 类型 | 说明 |
|------|------|------|
| rating | INT 1-5 | 星级评分 |
| useful | TINYINT 0/1 | 是否有用 |
| feedback | VARCHAR(500) | 文字反馈 |

统计接口返回：总问答数、平均评分、有用率等聚合指标。

---

### 模块 4：AI 模型编排

#### 3.4.1 涉及文件

| 层次 | 文件 | 路径 |
|------|------|------|
| Service | `M3Service.java` | `.../service/M3Service.java` |
| Service Impl | `M3ServiceImpl.java` | `.../service/impl/M3ServiceImpl.java` |
| Client | `M3Client.java` | `.../client/M3Client.java` |
| Client Impl | `DefaultM3Client.java` | `.../client/DefaultM3Client.java` |
| Exception | `M3Exception.java` | `.../client/M3Exception.java` |
| DTO | `ChatRequest.java` | `.../client/dto/ChatRequest.java` |
| DTO | `ChatResponse.java` | `.../client/dto/ChatResponse.java` |
| DTO | `QaResult.java` | `.../client/dto/QaResult.java` |
| DTO | `RankedHit.java` | `.../client/dto/RankedHit.java` |
| Config | `M3Properties.java` | `.../config/M3Properties.java` |
| Config | `DeepSeekProperties.java` | `.../config/DeepSeekProperties.java` |
| Config | `ModelRegistry.java` | `.../config/ai/ModelRegistry.java` |
| Config | `RestClientConfig.java` | `.../config/RestClientConfig.java` |

#### 3.4.2 M3Service 核心能力

| 方法 | 功能 | Fallback 策略 |
|------|------|--------------|
| `classifyWithFallback()` | 文档自动分类 | 返回默认分类 |
| `summarizeWithFallback()` | 文档摘要生成 | 截取文本前 N 字符 |
| `extractTagsWithFallback()` | 标签自动抽取 | 返回空列表 |
| `rerankWithFallback()` | 检索结果重排 | TF 词频打分 |
| `answerWithFallback()` | 基于上下文的问答 | 构造简单回退答案 |
| `answerStream()` | 流式问答 (Flux\<String\>) | — |

#### 3.4.3 支持的 AI 模型

在 `application.yml` 中预配置：

| 模型标识 | 提供方 | 流式支持 |
|----------|--------|---------|
| MiniMax-M3 | MiniMax | 是 |
| gpt-4o | OpenAI | 是 |
| gpt-3.5-turbo | OpenAI | 是 |
| deepseek-v4-pro | DeepSeek | 是 |
| deepseek-chat | DeepSeek | 是 |

运行时可通过 `POST /api/models/switch?model=xxx` 切换激活模型。

#### 3.4.4 DefaultM3Client 调用方式

- 基于 Spring `RestClient` 直调各 AI 厂商的 Chat Completions API
- 统一适配为内部 `QaResult` 结构（answer + citations）
- 超时配置：连接超时 10s，读取超时 60s，最大重试 1 次

---

### 模块 5：通用基础设施

#### 3.5.1 统一响应体

```
Result<T>
├── code: int        # 状态码
├── message: String  # 提示信息
└── data: T          # 业务数据

PageResult<T> extends Result
├── list: List<T>    # 数据列表
├── total: long      # 总记录数
├── page: long       # 当前页
└── size: long       # 每页大小
```

#### 3.5.2 状态码常量 (ResultCode)

| 常量 | 值 | 含义 |
|------|-----|------|
| SUCCESS | 200 | 成功 |
| BAD_REQUEST | 400 | 参数错误 |
| UNAUTHORIZED | 401 | 未登录 |
| FORBIDDEN | 403 | 无权限 |
| NOT_FOUND | 404 | 资源不存在 |
| INTERNAL_ERROR | 500 | 服务器内部错误 |

#### 3.5.3 全局异常处理器

`GlobalExceptionHandler` (`@RestControllerAdvice`) 统一捕获：

- `BizException` → 业务异常（返回对应 code + message）
- `IllegalArgumentException` → 400 参数错误
- `Exception` → 500 兜底

---

### 模块 6：数据库

#### 3.6.1 表结构

| 表名 | 说明 | 关键字段 |
|------|------|----------|
| `user` | 用户表 | id, username, password(BCrypt), role(USER/ADMIN) |
| `document` | 文档元信息 | id, title, file_path, file_type, category, tags, summary, status, owner_id |
| `document_chunk` | 文档分块 | id, document_id(FK), chunk_index, content, char_count |
| `qa_history` | 问答历史 | id, owner_id, question, answer, citations(JSON), rating, useful, feedback |

#### 3.6.2 数据库脚本

| 文件 | 路径 | 说明 |
|------|------|------|
| `schema.sql` | `resources/db/schema.sql` | 建库建表，幂等（`IF NOT EXISTS`） |
| `data-seed.sql` | `resources/db/data-seed.sql` | 种子用户：admin / test |

**默认账号**：

| 用户名 | 密码 | 角色 |
|--------|------|------|
| admin | Admin@123456 | ADMIN |
| test | Test@123456 | USER |

---

## 四、配置项速查 (`application.yml`)

### 服务器

```yaml
server.port: 8080
server.tomcat.max-http-form-post-size: 25MB
spring.servlet.multipart.max-file-size: 20MB
```

### 数据库

```yaml
spring.datasource.url: jdbc:mysql://localhost:3306/doc_summary
spring.datasource.username: root
```

### MyBatis-Plus

```yaml
mybatis-plus.configuration.map-underscore-to-camel-case: true
mybatis-plus.global-config.db-config.id-type: AUTO
```

### JWT

```yaml
app.jwt.secret: doc-summary-kb-jwt-secret-key-2024-change-in-production
app.jwt.expiration-ms: 604800000   # 7 天
```

### 文档处理

```yaml
app.upload-dir: ${user.dir}/uploads
app.chunk-size: 1000                # 分块字符数阈值
app.search.max-candidates: 30
app.search.snippet-radius: 80
```

### API Key

API Key 存放在 `backend/secrets.properties`（已加入 `.gitignore`），项目提供模板 `secrets.properties.example`。启动时通过 `spring.config.import` 加载：

```yaml
spring.config.import: optional:file:./secrets.properties
```

secrets.properties 格式：
```properties
mini-max.api-key=sk-your-key-here
deepseek.api-key=sk-your-key-here
```
`M3Properties.resolveApiKey()` 优先读环境变量 `MINIMAX_API_KEY`，其次读配置文件。

### AI 模型

```yaml
ai.active-model: MiniMax-M3         # 当前激活模型
ai.models:                          # 可用模型列表
  - name: MiniMax-M3
  - name: gpt-4o
  - name: deepseek-v4-pro
  ...
```

### 跨域

```yaml
app.cors.allowed-origins:
  - http://localhost:5173
```

---

## 五、API 路由总览

| 模块 | 路由前缀 | Controller |
|------|----------|------------|
| 健康检查 | `GET /api/health` | `HealthController` |
| 认证 | `POST /api/auth/*` | `AuthController` |
| 用户管理 | `/api/admin/users/**` | `UserController` |
| 文档管理 | `/api/documents/**` | `DocumentController` |
| 智能问答 | `/api/qa/**` | `QaController` |
| 模型管理 | `/api/models/**` | `ModelController` |

---

## 六、关键设计决策

1. **权限感知贯穿全链路** — 所有涉及文档数据的接口（列表、详情、检索、问答）均根据当前用户角色过滤可见范围，避免数据泄露。

2. **AI 调用全带 Fallback** — 每个 AI 能力（分类/摘要/标签/重排/问答）均设计降级策略，AI 不可用时系统仍可正常运作。

3. **异步解耦上传与 AI 处理** — 文档上传后立即返回，AI 增强（分类/摘要/标签）通过 `@Async` 异步执行，失败仅标记状态，不阻塞主流程。

4. **多模型热切换** — 通过 `ModelRegistry` 在运行时切换 AI 模型，无需重启服务。不同模型可指定不同提供方（MiniMax/OpenAI/DeepSeek）。

5. **流式问答** — 通过 Spring WebFlux `Flux<String>` + SSE 实现逐 token 推送，提升用户体验。

6. **评测闭环** — 问答记录支持评分/是否有用/文字反馈，提供统计接口，支撑模型效果持续优化。
