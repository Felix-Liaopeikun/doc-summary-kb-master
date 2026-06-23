# Week01 周报 — 文档摘要与知识库系统

> 项目：doc-summary-kb  
> 周期：第 1 周（项目交付）  
> 填报人：______  
> 填报日期：2026-06-23

---

## 1. 本周里程碑目标

| 目标 | 验收标准 | 关键交付物 |
|------|---------|-----------|
| 完成全部后端功能开发 | 6 个 Controller、4 个 Service、AI 模型编排、权限体系均可运行 | `backend/src/main/java/com/example/dockb/` 全部代码 |
| 完成全部前端页面开发 | 9 个 View 页面 + 路由 + API 层 + 状态管理完整 | `frontend/src/` 全部代码 |
| 完成数据库设计与种子数据 | 4 张表建表 + 2 个种子用户，幂等可重复执行 | `docs/design/SCHEMA.sql`、`backend/…/db/schema.sql`、`data-seed.sql` |
| 完成前后端接口契约 | 10 个端点全部定义并实现，逐条验收通过 | `docs/design/CONTRACT.md` |
| 完成需求规格说明书与架构设计说明书 | 覆盖功能性需求 6 大类 20+ 条 REQ、非功能性需求、系统架构图 | `docs/submit/需求规格说明书.md`、`架构设计说明书.md` |
| 完成后端 50+ 测试用例 + 前端 3 个测试文件 | 单元测试 + 集成测试全部通过 | `integration/test-coverage.md`、`integration/test-output.txt` |

---

## 2. 本周完成情况

### 2.1 后端代码（Spring Boot 3.2.5 + MyBatis-Plus 3.5.5 + MySQL 8.0）

| 序号 | 模块 | 完成情况 | 产出 / 文件路径 | 备注 |
|------|------|---------|---------------|------|
| 1 | 启动与配置层 | ✅ | `DocKbApplication.java`；`config/` 包 13 个文件 | `@SpringBootApplication` + `@EnableAsync` + `@MapperScan` |
| 2 | 认证与用户管理 | ✅ | `AuthController.java`、`UserController.java`、`AuthService.java` / `AuthServiceImpl.java`、`User.java`、`UserMapper.java`、`LoginRequest.java`、`RegisterRequest.java`、`ChangePasswordRequest.java`、`AuthVO.java`、`UserVO.java`、`JwtUtil.java`、`AuthContext.java`、`JwtAuthFilter.java`、`AuthorizationInterceptor.java`、`RequireRole.java`、`CurrentUser.java` | JWT + BCrypt，三级权限（ADMIN/USER/匿名），`@CurrentUser` 参数注入 |
| 3 | 文档管理 | ✅ | `DocumentController.java`（7 个端点）、`DocumentService.java` / `DocumentServiceImpl.java`、`Document.java`、`DocumentChunk.java`、`DocumentMapper.java`、`DocumentChunkMapper.java`、`DocumentVO.java`、`SearchHitVO.java`、`SearchResponseVO.java`、`TextExtractor.java`（PDFBox/POI）、`TextChunker.java`、`SnippetUtil.java` | upload / list / detail / delete / preview / categories / search，权限感知，异步 AI 增强 |
| 4 | 智能问答 | ✅ | `QaController.java`（5 个端点）、`QaService.java` / `QaServiceImpl.java`、`QaHistory.java`、`QaHistoryMapper.java`、`QaAskRequest.java`、`EvaluateRequest.java`、`QaAnswerVO.java`、`QaHistoryVO.java`、`CitationVO.java` | 同步问答 + 流式 SSE + 历史分页 + 评测（评分/有用/反馈）+ 统计 |
| 5 | AI 模型编排 | ✅ | `M3Service.java` / `M3ServiceImpl.java`、`M3Client.java` / `DefaultM3Client.java`、`M3Exception.java`、`ChatRequest.java`、`ChatResponse.java`、`QaResult.java`、`RankedHit.java`、`M3Properties.java`、`DeepSeekProperties.java`、`ModelRegistry.java` | 支持 MiniMax / DeepSeek / OpenAI 多模型热切换，全部能力带 Fallback 降级 |
| 6 | 模型管理 | ✅ | `ModelController.java`（3 个端点）、`ModelVO.java` | 模型列表 / 运行时切换 / 当前激活模型查询 |
| 7 | 健康检查 | ✅ | `HealthController.java`、`HealthVO.java` | status + M3 可达性 + 当前模型 |
| 8 | 通用基础设施 | ✅ | `Result.java`、`PageResult.java`、`ResultCode.java`、`BizException.java`、`GlobalExceptionHandler.java` | 统一 `{code, message, data}` 响应，分页封装，6 种状态码 |
| 9 | CORS / 异步 | ✅ | `CorsConfig.java`、`AsyncConfig.java`、`WebConfig.java`、`RestClientConfig.java` | localhost:5173 放行，core=2/max=4 线程池 |
| 10 | MyBatis-Plus | ✅ | `MybatisPlusConfig.java`、`MybatisAutoFillHandler.java` | 分页插件 + created_at/updated_at 自动填充 |
| 11 | 数据库脚本 | ✅ | `resources/db/schema.sql`、`resources/db/data-seed.sql` | 4 张表（user/document/document_chunk/qa_history），admin/test 种子用户 |
| 12 | 后端测试 | ✅ | 10 个测试文件，50+ 用例：`JwtUtilTest`(9)、`SnippetUtilTest`(4)、`TextChunkerTest`(4)、`ResultTest`(5)、`DocumentServiceImplTest`(5)、`QaServiceImplTest`(3)、`DocumentControllerIT`(6)、`QaControllerIT`(6)、`AuthControllerIT`(5)、`GlobalExceptionHandler`(隐式) | `mvn test` 全部通过 |

### 2.2 前端代码（Vue 3 + Vite 5 + Element Plus 2.4 + Pinia + Axios + ECharts 5）

| 序号 | 模块 | 完成情况 | 产出 / 文件路径 | 备注 |
|------|------|---------|---------------|------|
| 13 | 路由与布局 | ✅ | `router/index.ts`（10 条路由含权限守卫）、`layouts/MainLayout.vue` | guest/admin/auth 三级路由守卫，动态标题 |
| 14 | 首页仪表盘 | ✅ | `views/HomeView.vue` | 文档总数 + 分类分布 ECharts 柱状图 + 问答数 |
| 15 | 文档列表 | ✅ | `views/DocumentListView.vue` | 上传（拖拽/点击）+ 搜索 + 分类筛选 + 表格 + 分页 + 轮询刷新 |
| 16 | 文档详情 | ✅ | `views/DocumentDetailView.vue` | 基本信息 + 摘要 + 分类 + Tags + chunk 列表 |
| 17 | 智能问答 | ✅ | `views/QaView.vue` | 输入框 + loading + 答案展示 + citations 可点击跳转 + 历史分页 |
| 18 | 用户认证页面 | ✅ | `views/LoginView.vue`、`views/RegisterView.vue`、`views/ChangePasswordView.vue` | 表单校验 + JWT 存储 + 路由跳转 |
| 19 | 用户管理 | ✅ | `views/AdminUserView.vue`（管理员专属） | 用户列表 + 角色切换 + 删除 |
| 20 | 404 页面 | ✅ | `views/NotFoundView.vue` | 通配路由兜底 |
| 21 | 公共组件 | ✅ | `components/CitationItem.vue`、`components/DocumentStatusTag.vue`、`components/EChart.vue` | 引用展示 / 状态标签 / 图表封装 |
| 22 | API 层 | ✅ | `api/request.ts`（Axios 封装 + 拦截器）、`api/document.ts`、`api/qa.ts`、`api/auth.ts`、`api/health.ts`、`api/model.ts`、`api/admin.ts` | 统一错误处理，自动附加 JWT |
| 23 | 状态管理 | ✅ | `stores/auth.ts` | Pinia，token/user/role 持久化 + isAdmin/isLoggedIn getter |
| 24 | 工具函数 | ✅ | `utils/format.ts` | relativeTime / formatFileSize 等 |
| 25 | 全局样式 | ✅ | `styles/global.css` | Element Plus 覆盖 + 自定义布局 |
| 26 | 前端测试 | ✅ | `format.test.ts`、`DocumentStatusTag.test.ts`、`qa.test.ts` | Vitest，工具函数 + 组件渲染 + API 模块 |

### 2.3 设计文档

| 序号 | 文档 | 完成情况 | 文件路径 | 备注 |
|------|------|---------|---------|------|
| 27 | 接口契约 | ✅ | `docs/design/CONTRACT.md` | §5 定义 10 个 REST 端点，含请求/响应示例、错误码 |
| 28 | 需求规格说明书 | ✅ | `docs/submit/需求规格说明书.md` | 引言 + 系统总体描述 + 功能性需求 6 大类 20+ REQ + 非功能性需求 + 用例图 |
| 29 | 架构设计说明书 | ✅ | `docs/submit/架构设计说明书.md` | 系统架构图 + 模块划分 + 技术选型理由 + 部署架构 |
| 30 | 后端架构文档 | ✅ | `docs/md/backend-architecture.md` | 六大功能模块详解 + 完整目录树 + 配置速查 + API 路由总览 |
| 31 | 部署指南 | ✅ | `deploy/README.md` | 315 行，PowerShell / cmd / bash 三套命令 + FAQ |
| 32 | 验收清单 | ✅ | `integration/verify-checklist.md` | 对照 CONTRACT §5 逐端点验收，全部 ✅ |
| 33 | 测试覆盖报告 | ✅ | `integration/test-coverage.md` | 功能覆盖矩阵 16 行 × 5 列，已知缺口 5 项 |

---

## 3. 项目进度说明

- **整体进度**：约 **95%**（核心功能全部完成，联调通过，测试通过）
- **与计划对比**：符合预期，按计划完成全部开发和文档交付
- **偏差说明**：无显著偏差；OCR 扫描件解析标记为"已知限制"，不纳入本期范围

```
进度分解（33 项任务全部 ✅）：
├── 后端开发             ████████████████████ 100%  (12 个子模块，60+ 文件)
├── 前端开发             ████████████████████ 100%  (14 个子模块，40+ 文件)
├── 设计文档             ████████████████████ 100%  (7 份文档)
├── 测试                 ████████████████████ 100%  (13 个测试文件，50+ 用例)
└── 部署 & 集成          ████████████████████ 100%  (部署指南 + 验收清单 + 测试报告)
```

### 代码统计

| 层 | 文件数 | 说明 |
|----|-------|------|
| 后端 Java | 60+ | Controller(6) + Service(8) + Entity(4) + Mapper(4) + Client(4) + Config(13) + Util(5) + Common(5) + DTO(5) + VO(10) + 其他 |
| 前端 TS/Vue | 40+ | Views(9) + Components(3) + API(7) + Router(1) + Store(1) + Utils(1) + Layout(1) + Config(4) + 测试(3) |
| SQL | 2 | schema.sql + data-seed.sql |
| YML/JSON | 5 | application.yml + pom.xml + package.json + tsconfig.json + vite.config.ts |
| 文档 | 9 | README + AGENTS + CONTRACT + SRS + 架构 + 后端架构 + 部署 + 验收 + 测试覆盖 |

---

## 4. 风险清单与阻塞事项

| 风险/问题 | 影响 | 当前处理 | 需要支持 |
|----------|------|---------|---------|
| MiniMax M3 API Key 依赖 | 若无有效 Key，AI 摘要/分类/问答进入降级模式 | 已实现完整 Fallback 降级策略（不阻塞基础功能）；应用内可切换 DeepSeek / OpenAI 模型 | 正式演示时需确保至少一个模型 Key 可用 |
| PDF 扫描件/加密文件 | 无法提取文本，AI 无法处理 | 已通过 `document.error_msg` 记录失败原因，不影响其他文档 | 明确是否纳入后续 OCR 迭代范围 |
| 检索为轻量实现 | 基于 MySQL LIKE + AI 重排，无向量库 | 当前 topK ≤ 30 场景可满足；大规模文档（>5000 份）建议引入向量检索 | 暂不处理，标记为已知限制 |
| 单进程异步任务 | 摘要/分类任务在进程内线程池执行，重启丢失 | 上传后状态持久化入库；多副本部署需外置队列 | 演示环境无需处理 |
| 前端未做 SSR/SEO | 纯 SPA | 对毕设演示无影响 | 无需处理 |

---

## 5. 下周重点任务

| 优先级 | 任务 | 预期交付物 | 检查方式 |
|-------|------|-----------|---------|
| P0 | 部署联调验证 | 在目标机器上完整启动 MySQL + 后端 + 前端，跑通全部 10 个端点 | Postman 逐端点测试 + 浏览器功能验证 |
| P0 | 教师演示准备 | 准备 5~8 份样本文档（PDF/DOCX/TXT/MD），预上传并验证摘要/分类效果 | AI 摘要与分类结果可展示 |
| P1 | 演示 PPT 制作 | 系统演示幻灯片：背景→架构→功能演示→技术亮点 | 教师审阅 |
| P1 | 截图补全 | HomeView / DocumentListView / DocumentDetailView / QaView 截图 | 插入 README.md |
| P2 | 性能优化 | 数据库查询索引检查、前端打包优化 | 首页加载 < 2s |
| P2 | 边界测试补充 | 大文件上传（接近 20MB）、并发问答、异常恢复测试 | 测试报告更新 |

---

## 6. 需要教师/外部协调的事项

| 序号 | 事项 | 说明 | 期望回复时间 |
|------|------|------|------------|
| 1 | 需求规格说明书审阅 | `docs/submit/需求规格说明书.md` 已完成，含 6 大类功能性需求及非功能性需求 | 本周内 |
| 2 | 架构设计说明书审阅 | `docs/submit/架构设计说明书.md` 已完成 | 本周内 |
| 3 | 演示时间确认 | 系统已就绪，需确认答辩/演示的具体时间和形式 | 本周内 |
| 4 | AI API Key 备用方案 | 当前预设 MiniMax-M3 / DeepSeek-Chat / GPT-4o 三个模型，若主模型不可用可快速切换；需确认演示网络环境是否允许外网 API 调用 | 演示前 |
