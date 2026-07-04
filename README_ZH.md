# Domain Architecture Plugin

中文 ｜ [English](README.md)

---

面向业务领域软件系统的插件优先架构指导包。它帮助 AI 编程 agent 从业务需求走到领域模型、架构决策和框架落地，同时避免把 DDD、Hexagonal Architecture、Onion Architecture、CQRS 或某个框架约定混成一套强制组合模型。

分发单元是 `domain-architecture` 插件。`skills/` 目录是插件内部能力，安装插件后由 Codex、Claude Code 和其它兼容 agent 暴露出来。

## 插件能力

| Skill                         | 作用                                                                                                                                                                   |
|-------------------------------|------------------------------------------------------------------------------------------------------------------------------------------------------------------------|
| `domain-architecture-workflow` | 端到端业务领域架构入口工作流，编排建模、架构判断和可选框架落地。                                                                                                       |
| `domain-modeling`             | 框架无关的领域建模流程，覆盖统一语言、命令、事件、限界上下文、聚合、不变量、值对象、领域服务、仓储和读模型。                                                           |
| `domain-architecture-guidance` | 来源感知的架构指导，覆盖 DDD、Layered、Onion、Hexagonal / Ports and Adapters、CQRS、jMolecules 风格注解和架构测试。                                                    |
| `use-jfoundry`               | [jfoundry](https://github.com/xfoundries/jfoundry) 专用 Java 业务项目指导，覆盖依赖、包结构、注解、Repository/Port 边界、持久化适配器、Outbox/Inbox 和 ArchUnit 规则。 |

## 推荐工作流

使用插件内的 `domain-architecture-workflow` 能力作为通用入口：

1. 理解业务目标、参与者、流程、约束和不确定性。
2. 对非平凡业务行为使用 `domain-modeling` 先建模。
3. 使用 `domain-architecture-guidance` 判断是否适合 DDD、Hexagonal、Onion、CQRS、ports/adapters 或更简单的 CRUD。
4. 领域和架构假设清楚后，再使用框架专用指导。只有 [jfoundry](https://github.com/xfoundries/jfoundry) 项目才使用 `use-jfoundry`。
5. 当实现触及边界时，用架构测试、代码评审或明确风险说明做验证。

这个插件不依赖任何外部工作流系统。它可以和 planning、TDD、code review 或 superpowers 风格工作流一起使用：如果另一个流程插件或 skill 已经激活，只在其中的领域和架构决策节点使用本插件。

## 适用范围

主要适用：

- Java / Kotlin 后端系统
- C# / .NET 后端系统
- Go 后端系统
- Python 后端系统

有条件适用：

- Dart / Flutter
- Swift / iOS
- 其它包含复杂本地业务规则、离线同步、本地持久化边界或复杂领域逻辑的客户端应用

不要把 DDD、ports/adapters、CQRS、repository 或分层项目结构强行套到简单 CRUD、薄客户端或小脚本里。

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
codex plugin marketplace add xfoundries/software-architecture-skills
codex plugin add domain-architecture@xfoundries
```

如果从当前 checkout 做本地开发：

```bash
codex plugin marketplace add /Users/huangxiao/Workspace/mine/software-architecture-skills
codex plugin add domain-architecture@xfoundries
```

兼容 `.agents/plugins/marketplace.json` 的其它 agent 也可以使用同样形态。marketplace 条目指向仓库根目录：

```json
{
  "name": "xfoundries",
  "interface": {
    "displayName": "Domain Architecture"
  },
  "plugins": [
    {
      "name": "domain-architecture",
      "source": {
        "source": "url",
        "url": "./"
      },
      "policy": {
        "installation": "AVAILABLE",
        "authentication": "ON_INSTALL"
      },
      "category": "Productivity"
    }
  ]
}
```

如果需要使用 `~/.agents/plugins` 下的个人 marketplace，也可以让 `~/plugins/domain-architecture` 指向本仓库并添加个人 marketplace 条目。但仓库内置 marketplace 是更推荐的项目自带分发形态。

### Claude Code

Claude Code 可以通过插件系统校验和安装同一个插件源码。本仓库包含 `.claude-plugin/plugin.json`、`.claude-plugin/marketplace.json`，并复用同一份 `skills/` 能力。

```bash
claude plugin validate /Users/huangxiao/Workspace/mine/software-architecture-skills
claude plugin marketplace add xfoundries/software-architecture-skills
claude plugin install domain-architecture@xfoundries
```

如果从当前 checkout 做本地开发：

```bash
claude plugin marketplace add /Users/huangxiao/Workspace/mine/software-architecture-skills
claude plugin install domain-architecture@xfoundries
```

### 原始 skill 兼容

不支持插件的 agent 仍然可以把 `skills/` 下的目录当作普通 `SKILL.md` skills 使用。这是兼容兜底，不是推荐安装形态。

## 使用示例

```text
Use $domain-architecture-workflow to guide this order-management project from requirements to architecture decisions.
```

```text
Use $domain-modeling to turn these billing requirements into commands, events, aggregates, invariants, and open questions.
```

```text
Use $domain-architecture-guidance to review this Java service and tell me whether the aggregate boundaries and Hexagonal dependencies are justified.
```

```text
Use $use-jfoundry to implement the confirmed model in a Java 21 jfoundry project with Hexagonal Architecture and ArchUnit tests.
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
  use-jfoundry/
```

## 更新

本地开发时，保持目标 agent 的 marketplace source 指向本仓库即可。修改插件元数据后，在目标 agent 中重新安装或更新插件，让它刷新缓存。

对 Codex 来说，必要时更新 `.codex-plugin/plugin.json` 的 cachebuster，然后从 `domain-architecture@xfoundries` 重新安装。

## 设计原则

用架构模式保护业务含义和变化边界，不要把它们当成装饰性目录结构。
