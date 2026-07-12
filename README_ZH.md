# Domain Architecture Plugin

中文 ｜ [English](README.md)

---

面向业务领域软件系统的插件优先架构指导包。它帮助 AI 编程 agent 从业务需求走到领域模型、架构决策和框架落地，同时避免把 DDD、Hexagonal Architecture、Onion Architecture、CQRS 或某个框架约定混成一套强制组合模型。

分发单元是 `domain-architecture` 插件。`skills/` 目录是插件内部能力，安装插件后由 Codex、Claude Code 和其它兼容 agent 暴露出来。

## 核心能力

| Skill | 核心职责 | 适用范围 |
|---|---|---|
| `domain-architecture-workflow` | 默认端到端协调器，负责阶段路由并生成 `Domain Architecture Handoff`。 | 与语言、框架无关。 |
| `domain-modeling` | 为业务语言、行为、边界和 Invariant 生成 `Domain Modeling Result`。 | 与语言、框架无关。 |
| `domain-architecture-guidance` | 为架构选择、依赖规则、CQRS 和验证生成 `Architecture Guidance Result`。 | 原则与语言无关；Java/Kotlin 指导最具体。 |
| `using-jfoundry` | 为依赖、结构、Port、持久化、事务、异常边界、消息和测试生成 `JFoundry Implementation Guidance Result`。 | 仅适用于 Java 和 jfoundry。 |

## 如何使用

端到端任务从 `domain-architecture-workflow` 开始：

```text
requirements
-> domain-architecture-workflow
-> Domain Modeling Result
-> Architecture Guidance Result
-> optional JFoundry Implementation Guidance Result
-> Domain Architecture Handoff
```

`Domain Architecture Handoff` 是协调器交给后续 planning、implementation 或 review 活动的组合结果。它保留专业结果和阶段状态，但不替代专业结果，也不要求固定文件格式。

### 独立使用

```text
Use $domain-architecture-workflow for this business project.
Business goal and known rules:
Existing artifacts and constraints:
JFoundry: yes | no | undecided
Desired next activity:
```

如果只处理单一问题，可以直接调用 `domain-modeling`、`domain-architecture-guidance` 或 `using-jfoundry`。

### 与 Process Companion 组合

Superpowers、SpecKit、OpenSpec 等工作流都是可选 Process Companion。它们负责自己的 specification、planning、task、implementation、review、文件和命令；本插件负责 Domain Modeling、Architecture Guidance、可选 jfoundry landing 和 Handoff。

```text
Use <process companion> for the development process.
Use $domain-architecture-workflow for domain and architecture decisions after
requirements are understood and before dependent planning or tasks. Feed
Domain Architecture Handoff into the companion's next activity.
```

如果 Blocker 改变了业务含义或架构，应将它返回 Process Companion 管理的 requirements 或 specification 活动，而不是猜测。完整的输入、责任归属、状态和返回规则参见 [first-use guide](skills/domain-architecture-workflow/references/first-use.md)。

## 适用范围与限制

- 核心 Domain Modeling 和 Architecture Guidance 方法与语言、框架无关，但 Java/Kotlin 的实现指导最深入。C#/.NET、Go、Python 提供映射和示例，但集成与模板较少；`using-jfoundry` 仅适用于 Java。
- 主要面向业务后端软件。对于包含大量离线 Domain Logic、同步流程、持久化边界或复杂规则的客户端应用，也可以按需使用。
- 不要把 DDD、Ports and Adapters、CQRS、Repository 或 Layered Architecture 强行套到简单 CRUD、薄客户端或小脚本中。

## 资料来源策略

架构指导把资料分为三层：

- 基础来源：Eric Evans 用于 DDD，Alistair Cockburn 用于 Hexagonal Architecture，Martin Fowler 用于企业应用模式和 CQRS 讨论，Greg Young 用于 CQRS，Jeffrey Palermo 用于 Onion Architecture，Clean Architecture 只谨慎用于依赖方向综合。
- 广泛使用的实现指导：jMolecules、Microsoft .NET architecture guidance、Spring Modulith、ArchUnit、ArchUnitNET、microservices.io。
- 带个人观点的综合模型和示例：可以参考，但不能作为权威标准。

这个插件区分 DDD 建模概念、架构风格约束和框架约定，不会把 DDD、Layered、Onion、Hexagonal、CQRS 和 Event Sourcing 表述成一个标准架构。

## 安装

### Codex 与 `.agents/plugins` 兼容 agent

本仓库内置 `.agents/plugins/marketplace.json`，因此可以直接作为本地或 Git marketplace 添加：

```bash
codex plugin marketplace add xfoundries/domain-architecture-skills
codex plugin add domain-architecture@xfoundries
```

如果从当前 checkout 做本地开发：

```bash
codex plugin marketplace add .
codex plugin add domain-architecture@xfoundries
```

兼容 agent 可以直接使用仓库自带的 [marketplace manifest](.agents/plugins/marketplace.json)，其中的条目指向插件根目录。

### Claude Code

Claude Code 可以通过插件系统校验和安装同一个插件源码。本仓库包含 `.claude-plugin/plugin.json`、`.claude-plugin/marketplace.json`，并复用同一份 `skills/` 能力。

```bash
claude plugin validate .
claude plugin marketplace add xfoundries/domain-architecture-skills
claude plugin install domain-architecture@xfoundries
```

如果从当前 checkout 做本地开发：

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

本地开发时，保持目标 agent 的 marketplace source 指向本仓库即可。修改插件元数据后，在目标 agent 中重新安装或更新插件，让它刷新缓存。

对 Codex 来说，必要时更新 `.codex-plugin/plugin.json` 的 cachebuster，然后从 `domain-architecture@xfoundries` 重新安装。

## 设计原则

用架构模式保护业务含义和变化边界，不要把它们当成装饰性目录结构。
