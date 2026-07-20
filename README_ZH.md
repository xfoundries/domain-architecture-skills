# 领域架构插件

中文 ｜ [English](README.md)

---

面向业务领域软件系统的插件优先架构指导包。它帮助 AI 编程智能体从业务需求走到领域模型、架构决策和框架落地，同时避免把 DDD、Hexagonal Architecture、Onion Architecture、CQRS 或某个框架约定混成一套强制组合模型。

分发单元是 `domain-architecture` 插件。`skills/` 目录是插件内部能力，安装插件后由 Codex、Claude Code 和其它兼容智能体暴露出来。

## 核心能力

| 技能 | 核心职责 | 适用范围 |
|---|---|---|
| `domain-architecture-workflow` | 默认端到端协调器，负责阶段路由并生成领域架构交接。 | 与语言、框架无关。 |
| `domain-modeling` | 为业务语言、行为、边界和不变量生成领域建模结果（`Domain Modeling Result`）。 | 与语言、框架无关。 |
| `domain-architecture-guidance` | 为架构选择、依赖规则、CQRS 和验证生成架构指导结果（`Architecture Guidance Result`）。 | 原则与语言无关；Java/Kotlin 指导最具体。 |
| `using-jfoundry` | 为依赖、结构、端口、持久化、事务、异常边界、消息和测试生成 jfoundry 落地指导结果（`JFoundry Implementation Guidance Result`）。 | 仅适用于 Java 和 jfoundry。 |

## 如何使用

端到端任务从 `domain-architecture-workflow` 开始：

```text
需求
-> domain-architecture-workflow
-> 领域建模结果（`Domain Modeling Result`）
-> 架构指导结果（`Architecture Guidance Result`）
-> 可选的 jfoundry 落地指导结果（`JFoundry Implementation Guidance Result`）
-> 领域架构交接
-> 详细规划（`docs/domain-architecture/plans/` 或用户选定的流程伴侣）
```

领域架构交接是协调器交给后续规划、实施或评审活动的组合结果。它保留专业结果和阶段状态，但不替代专业结果，也不要求固定文件格式。它会标明最小的规划就绪增量、其依赖的阻塞项和下一步规划所有者；它不是详细实施计划。新项目持久化插件产物时使用 `docs/domain-architecture/`，独立详细计划使用其 `plans/` 子目录。

所有项目都应有从需求到规划、验证的审慎流程，但领域建模和架构指导阶段应按增量复杂度裁剪：新建或领域复杂行为需要完整工作流；简单 CRUD 和局部修复复用既有证据，或记录为何无需更丰富的阶段。

### 独立使用

```text
请将 $domain-architecture-workflow 用于这个业务项目。
业务目标与已知规则：
现有产物与约束：
是否使用 jfoundry：是 | 否 | 未决定
期望的下一步活动：
```

如果只处理单一问题，可以直接调用 `domain-modeling`、`domain-architecture-guidance` 或 `using-jfoundry`。

`using-jfoundry` 负责把已确认的架构翻译为框架落地指导。它会保留架构指导结果（`Architecture Guidance Result`）、现有项目证据、足以支持简单变更的既有约定或用户明确选择；不会把尚未决定架构的项目默认设为 Hexagonal Architecture。
对于 Spring Boot 与 JPA，它还会明确实体扫描、应用自有迁移以及可靠消息重试语义。

其中的架构测试模板使用 ArchUnit 原生 `ArchTests`。聚合仓储（Repository）保持独立的 DDD 身份：Hexagonal 项目可以同时把它表达为次级端口（Secondary Port），而无需移出 `domain.repository`；Onion 项目则通过内环契约与基础设施环实现表达同一依赖倒置关系。

插件会区分命名规则的来源：Onion 不继承 Hexagonal 的主/次端口（Primary/Secondary Port）或适配器（Adapter）角色。Hexagonal 项目应在 `adapter.in/out` 与 `adapter.primary/secondary` 中选择一套，并作为适配器包约定守护；两套都不适用于 Onion。`Reader`、`Store`、`Finder`、`Provider` 等名称可以作为按职责表达的项目约定，但不是 DDD、Onion 或 jfoundry 官方模式；命名仍应首先来自通用语言。

其中的可靠消息指导会区分版本化集成契约与内部领域事件，要求使用不携带 Java 类型元数据的可移植 JSON，并在运行时验证所选消息代理适配器（broker adapter）确实优先于日志回退实现（logging fallback）。

如果是否使用 jfoundry 尚未决定，框架中立的领域建模和架构指导会继续进行，不调用 `using-jfoundry`。只有后续框架相关活动实质依赖这一选择时，工作流才会询问。

### 与流程伴侣（Process Companion）组合

Superpowers、SpecKit、OpenSpec 等工作流都是可选流程伴侣。它们负责自己的规格、规划、任务、实施、评审、文件和命令；本插件负责领域建模、架构指导、可选的 jfoundry 落地指导和交接。流程伴侣可以帮助形成初始需求材料，但在最终确定依赖领域或架构的 `plan`、`task` 前必须消费交接结果。

```text
使用 <流程伴侣名称> 管理开发流程。
在充分理解需求后、生成依赖这些结论的规划或任务之前，使用
$domain-architecture-workflow 完成领域与架构决策。
将领域架构交接交给该流程伴侣的下一项活动。
```

如果阻塞项改变了业务含义或架构，应将它返回流程伴侣管理的需求或规格活动，而不是猜测。完整的输入、责任归属、状态和返回规则参见[首次使用指南](skills/domain-architecture-workflow/references/first-use.md)。

没有选择流程伴侣时，交接结果会交给插件管理的详细规划活动，默认位于 `docs/domain-architecture/plans/`。项目也可以在完成独立分析后再引入 Superpowers、SpecKit 或 OpenSpec：它们消费既有交接结果；只有证据变化时才重跑已经完成的阶段。其它目录中的既有文档只作为输入证据，不能改变插件产物位置。

## 适用范围与限制

- 核心领域建模和架构指导方法与语言、框架无关，但 Java/Kotlin 的实现指导最深入。C#/.NET、Go、Python 提供生态映射而非代码模板；`using-jfoundry` 仅适用于 Java。
- 主要面向业务后端软件。客户端在拥有实质业务行为、离线工作流、同步冲突或本地持久化边界时可按需使用；本插件不提供移动端或前端平台的专项实现模板。
- 不要把 DDD、端口与适配器（Ports and Adapters）、CQRS、仓储（Repository）或分层架构（Layered Architecture）强行套到简单 CRUD、薄客户端或小脚本中。

## 资料来源策略

架构指导把资料分为三层：

- 基础来源：Eric Evans 用于 DDD，Alistair Cockburn 用于 Hexagonal Architecture，Martin Fowler 用于企业应用模式和 CQRS 讨论，Greg Young 用于 CQRS，Jeffrey Palermo 用于 Onion Architecture，Clean Architecture 只谨慎用于依赖方向综合。
- 广泛使用的实现指导：jMolecules、Microsoft .NET 架构指导、Spring Modulith、ArchUnit、ArchUnitNET、microservices.io。
- 带个人观点的综合模型和示例：可以参考，但不能作为权威标准。

这个插件区分 DDD 建模概念、架构风格约束和框架约定，不会把 DDD、Layered、Onion、Hexagonal、CQRS 和 Event Sourcing 表述成一个标准架构。

## 安装

### Codex 与 `.agents/plugins` 兼容智能体

本仓库内置 `.agents/plugins/marketplace.json`，因此可以直接作为本地或 Git marketplace 源添加：

```bash
codex plugin marketplace add xfoundries/domain-architecture-skills
codex plugin add domain-architecture@xfoundries
```

如果从当前检出目录做本地开发：

```bash
codex plugin marketplace add .
codex plugin add domain-architecture@xfoundries
```

兼容智能体可以直接使用仓库自带的 [marketplace 清单](.agents/plugins/marketplace.json)，其中的条目指向插件根目录。

### Claude Code

Claude Code 可以通过插件系统校验和安装同一个插件源码。本仓库包含 `.claude-plugin/plugin.json`、`.claude-plugin/marketplace.json`，并复用同一份 `skills/` 能力。

```bash
claude plugin validate .
claude plugin marketplace add xfoundries/domain-architecture-skills
claude plugin install domain-architecture@xfoundries
```

如果从当前检出目录做本地开发：

```bash
claude plugin marketplace add .
claude plugin install domain-architecture@xfoundries
```

## 仓库结构

```text
.codex-plugin/
  plugin.json
.claude-plugin/
  marketplace.json
  plugin.json
.agents/plugins/
  marketplace.json
skills/
  domain-architecture-workflow/
  domain-modeling/
  domain-architecture-guidance/
  using-jfoundry/
```

## 更新

本地开发时，保持目标智能体的 marketplace 源指向本仓库即可。修改插件元数据后，在目标智能体中重新安装或更新插件，让它刷新缓存。

对 Codex 来说，必要时更新 `.codex-plugin/plugin.json` 的缓存刷新版本字段，然后从 `domain-architecture@xfoundries` 重新安装。

## 设计原则

用架构模式保护业务含义和变化边界，不要把它们当成装饰性目录结构。
