# Software Architecture Skills

中文 ｜ [English](README.md)

---

面向业务领域软件架构的多 skill 方法论包。它帮助 AI 编程 agent 从业务需求走到领域模型、架构决策和框架落地，同时避免把 DDD、Hexagonal Architecture、Onion Architecture、CQRS 或某个框架约定混成一套强制组合模型。

这个仓库适用于支持 `SKILL.md` 结构的 agent，包括 Codex 风格的 skill 环境和 Claude Code 风格的本地 skills。

## Skills

| Skill                         | 作用 |
|-------------------------------|---|
| `domain-architecture-workflow` | 端到端业务领域架构入口工作流，编排建模、架构判断和可选框架落地。 |
| `domain-modeling`             | 框架无关的领域建模流程，覆盖统一语言、命令、事件、限界上下文、聚合、不变量、值对象、领域服务、仓储和读模型。 |
| `domain-architecture-guidance` | 来源感知的架构指导，覆盖 DDD、Layered、Onion、Hexagonal / Ports and Adapters、CQRS、jMolecules 风格注解和架构测试。 |
| `use-jfoundry`               | [jfoundry](https://github.com/xfoundries/jfoundry) 专用业务项目指导，覆盖依赖、包结构、注解、Repository/Port 边界、持久化适配器、Outbox/Inbox 和 ArchUnit 规则。 |

## 推荐工作流

使用 `domain-architecture-workflow` 作为通用入口：

1. 理解业务目标、参与者、流程、约束和不确定性。
2. 对非平凡业务行为使用 `domain-modeling` 先建模。
3. 使用 `domain-architecture-guidance` 判断是否适合 DDD、Hexagonal、Onion、CQRS、ports/adapters 或更简单的 CRUD。
4. 领域和架构假设清楚后，再使用框架专用指导。只有 [jfoundry](https://github.com/xfoundries/jfoundry) 项目才使用 `use-jfoundry`。
5. 当实现触及边界时，用架构测试、代码评审或明确风险说明做验证。

这个 skill 包不依赖任何外部工作流系统。它可以和 planning、TDD、code review 或 superpowers 风格工作流一起使用：如果另一个流程 skill 已经激活，只在其中的领域和架构决策节点使用这些 skills。

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

这些 skills 区分 DDD 建模概念、架构风格约束和框架约定，不会把 DDD、Layered、Onion、Hexagonal、CQRS 和 Event Sourcing 表述成一个标准架构。

## 安装

### skills.sh / skills CLI

安装整个仓库：

```bash
npx skills add youngledo/software-architecture-skills
```

安装到指定 agent：

```bash
npx skills add youngledo/software-architecture-skills -a claude-code
npx skills add youngledo/software-architecture-skills -a codex
npx skills add youngledo/software-architecture-skills -a cursor
npx skills add youngledo/software-architecture-skills -a opencode
```

安装单个 skill：

```bash
npx skills add youngledo/software-architecture-skills --skill domain-architecture-workflow
npx skills add youngledo/software-architecture-skills --skill domain-modeling
npx skills add youngledo/software-architecture-skills --skill domain-architecture-guidance
npx skills add youngledo/software-architecture-skills --skill use-jfoundry
```

查看可用 skills：

```bash
npx skills add youngledo/software-architecture-skills --list
```

### Codex 风格目录

复制全部 skills：

```bash
mkdir -p ~/.agents/skills
cp -R skills/* ~/.agents/skills/
```

如果环境使用 `~/.codex/skills`，改用该路径：

```bash
mkdir -p ~/.codex/skills
cp -R skills/* ~/.codex/skills/
```

### Claude Code 风格目录

用户级：

```bash
mkdir -p ~/.claude/skills
cp -R skills/* ~/.claude/skills/
```

项目级：

```bash
mkdir -p .claude/skills
cp -R skills/* .claude/skills/
```

Claude Code 不需要 `agents/openai.yaml`，保留即可。

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
skills/
  domain-architecture-workflow/
  domain-modeling/
  domain-architecture-guidance/
  use-jfoundry/
```

## 更新

通过 `skills` CLI 安装的 skills 不会被 Claude Code、Codex、Cursor 或 OpenCode 自动更新。仓库更新后，用户应运行：

```bash
npx skills update domain-architecture-workflow
npx skills update domain-modeling
npx skills update domain-architecture-guidance
npx skills update use-jfoundry
```

## 设计原则

用架构模式保护业务含义和变化边界，不要把它们当成装饰性目录结构。
