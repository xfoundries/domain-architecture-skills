# 领域架构插件

中文 ｜ [English](README.md)

---

面向业务领域软件系统的插件优先架构指导包。它帮助 AI 编程智能体从业务需求走到领域模型、架构决策，并在适用时提供框架落地指导；不会把 DDD、Hexagonal Architecture、Onion Architecture、CQRS 或某个框架约定混成一套强制组合模型。

## 快速开始

### Codex

```bash
codex plugin marketplace add xfoundries/domain-architecture-skills
codex plugin add domain-architecture@xfoundries
```

通过 `codex plugin list` 确认插件已出现，然后直接发送：

```text
请将 $domain-architecture-workflow 用于这个业务项目。

业务目标与已知规则：
现有项目或产物：
技术约束：
是否使用 jfoundry：是 | 否 | 未决定
期望的下一步活动：
```

工作流会利用已有证据，只询问阻碍负责决策的事实，并返回适用的专业结果和供规划、实施或评审使用的 `Domain Architecture Handoff`。

如果从当前检出目录做本地开发，改用本地来源：

```bash
codex plugin marketplace add .
codex plugin add domain-architecture@xfoundries
```

`xfoundries` marketplace 名称只能对应一个来源。要在本地检出与 Git 来源之间切换，先移除已有 marketplace：

```bash
codex plugin marketplace remove xfoundries
```

### Claude Code 与兼容智能体

Claude Code 可以校验并安装同一个插件源码：

```bash
claude plugin validate .
claude plugin marketplace add xfoundries/domain-architecture-skills
claude plugin install domain-architecture@xfoundries
```

仓库还提供适用于兼容智能体的 [`.agents/plugins` marketplace 清单](.agents/plugins/marketplace.json)。`skills/` 是插件内部能力，应安装 `domain-architecture` 插件，而非单独复制 skill。

## 它能做什么

端到端任务从 `domain-architecture-workflow` 开始：

```text
需求
-> 领域建模
-> 架构指导
-> 可选的 jfoundry 落地
-> Domain Architecture Handoff
-> 详细规划或用户选定的流程伴侣
```

交接会保留专业结果、决策、约束、开放问题和阻塞项，并标明最小的规划就绪增量及其下一步所有者；它是规划输入，不是详细实施计划。持久化的工作流产物位于 `docs/domain-architecture/`，独立详细计划位于其 `plans/` 子目录。

| 需求 | 入口 |
|---|---|
| 端到端的业务领域分析与交接 | `domain-architecture-workflow` |
| 业务语言、行为、不变量或边界 | `domain-modeling` |
| 架构决策或边界评审 | `domain-architecture-guidance` |
| 已确认的 jfoundry 实现落地 | `using-jfoundry` |

## 适用范围与限制

- 核心领域建模和架构指导方法与语言、框架无关，但 Java/Kotlin 的实现指导最深入。C#/.NET、Go、Python 提供生态映射而非代码模板；`using-jfoundry` 仅适用于 Java。
- 主要面向业务后端软件。客户端在拥有实质业务行为、离线工作流、同步冲突或本地持久化边界时可按需使用；本插件不提供移动端或前端平台的专项实现模板。
- 不要把 DDD、端口与适配器（Ports and Adapters）、CQRS、仓储（Repository）或分层架构（Layered Architecture）强行套到简单 CRUD、薄客户端或小脚本中。

## 进阶使用

- 只有在确认或明确要求 jfoundry 时才使用 `using-jfoundry`；框架尚未决定不会阻塞框架中立的领域建模和架构指导。其[架构落地说明](skills/using-jfoundry/references/architecture.md)保留已选架构风格，而不是替项目选择一种风格。
- Superpowers、SpecKit、OpenSpec 等流程伴侣是可选且由用户选择的。它们拥有自身的规格、规划、任务、实施、评审、文件和命令；本插件拥有专业结果和交接。[首次使用指南](skills/domain-architecture-workflow/references/first-use.md)定义了输入、责任归属、状态和返回规则。
- 已选择的架构风格保留自身约束。聚合仓储、适配器词汇、集成契约和可靠消息应遵循[架构约束](skills/domain-architecture-guidance/references/architecture-constraints.md)及适用 specialist reference；插件不会根据包名或可用框架能力推断这些选择。

## 资料来源策略

架构指导把资料分为三层：

- 基础来源：Eric Evans 用于 DDD，Alistair Cockburn 用于 Hexagonal Architecture，Martin Fowler 用于企业应用模式和 CQRS 讨论，Greg Young 用于 CQRS，Jeffrey Palermo 用于 Onion Architecture，Clean Architecture 只谨慎用于依赖方向综合。
- 广泛使用的实现指导：jMolecules、Microsoft .NET 架构指导、Spring Modulith、ArchUnit、ArchUnitNET、microservices.io。
- 带个人观点的综合模型和示例：可以参考，但不能作为权威标准。

这个插件区分 DDD 建模概念、架构风格约束和框架约定，不会把 DDD、Layered、Onion、Hexagonal、CQRS 和 Event Sourcing 表述成一个标准架构。

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
