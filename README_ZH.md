# DDD Architecture 指导

中文 ｜ [English](README.md)

---

面向业务领域系统的、重视资料来源层级的架构指导 skill，覆盖 Domain-Driven Design、Layered Architecture、Onion Architecture、Hexagonal Architecture / Ports and Adapters，以及 CQRS。

这个 skill 适用于支持 `SKILL.md` 结构的 AI 编程 agent，包括 Codex 风格的 skill 环境，以及 Claude Code 风格的本地 skills。

## 目的

这个 skill 用来帮助 AI agent 在设计、评审、重构、文档化或实现架构决策时，避免把所有 DDD 相关模式都混成一个所谓的 “Clean + DDD + Hexagonal + CQRS” 标准模型。

这个 skill 的主要实践参考是 [jMolecules](https://github.com/xmolecules/jmolecules)。jMolecules 是 Java 生态中用于在代码中表达 DDD building blocks 和架构风格的成熟开源项目。这个 skill 把 jMolecules 作为 Java/Kotlin 的强参考，也把它作为其它语言生态的概念参考，但仍然要求 agent 根据目标语言做生态化翻译，而不是把某个框架的结构复制到所有项目里。

在做架构论断时，它也优先参考原始资料或被广泛认可的资料。

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

这个 skill 不应该用来把 DDD、ports/adapters、CQRS、repository、分层项目结构强行套到简单 CRUD、薄移动端客户端或小脚本里。

## 覆盖内容

- DDD 构造块：bounded context、aggregate、entity、value object、repository、domain service、domain event
- Layered Architecture
- Onion Architecture
- Hexagonal Architecture / Ports and Adapters
- CQRS，但不默认绑定 Event Sourcing
- Java/Kotlin 中 jMolecules 风格的架构表达
- C#/.NET、Go、Python、Dart、Swift 中的生态化翻译
- 架构论断的资料来源层级和引用纪律

## 资料来源策略

这个 skill 把资料分为三层。

基础来源：

- Eric Evans 关于 Domain-Driven Design 的资料
- Alistair Cockburn 关于 Hexagonal Architecture / Ports and Adapters 的资料
- Martin Fowler 关于企业应用架构和 CQRS 的资料
- Greg Young 关于 CQRS 的资料
- Jeffrey Palermo 关于 Onion Architecture 的资料
- Robert C. Martin 关于 Clean Architecture 的资料，但只谨慎用于依赖方向和独立性原则

广泛使用的实现指导：

- [jMolecules](https://github.com/xmolecules/jmolecules)
- Microsoft Learn / Azure Architecture Center / .NET architecture guides
- Spring Modulith
- [ArchUnit](https://www.archunit.org/) / [ArchUnit Java 仓库](https://github.com/TNG/ArchUnit)
- [ArchUnitNET](https://github.com/TNG/ArchUnitNET)
- microservices.io

带有个人观点的综合模型和示例：

- Herberto Graca 的 Explicit Architecture 系列文章
- 把多种架构风格组合在一起的示例仓库
- 规定固定目录结构或通用规则的博客文章

带有个人观点的综合模型可以作为参考，但不应该被当成权威标准。

## 重要提醒

### Explicit Architecture

这个 skill 不把 “Explicit Architecture” 当成权威架构模型。

原因是它主要是一个个人综合模型：它把 DDD、Hexagonal Architecture、Onion Architecture、Clean Architecture、CQRS 以及相关模式组合在一起，并给这个组合命名。这种文章可以作为实践者参考，但它不是和 DDD、Hexagonal Architecture / Ports and Adapters、Onion Architecture、CQRS 同等级的广泛认可基础来源。

如果把它作为主要依据，容易让一个局部的个人组合看起来像行业标准。

### Clean Architecture

这个 skill 谨慎使用 Clean Architecture。

原因是 Clean Architecture 本身有意综合了 Hexagonal Architecture、Onion Architecture、BCE 等已有的边界和依赖方向思想。它的依赖规则和独立性目标有价值，但不应该被当成全新的、独立的架构，也不应该被理解成强制的四环目录结构。

当某个概念更直接属于 DDD、Hexagonal Architecture 或 Onion Architecture 时，这个 skill 要求 agent 优先回到对应的原始来源。

## 安装

### Codex 风格 skill 目录

复制 skill 文件夹到本地 skills 目录：

```bash
mkdir -p ~/.agents/skills
cp -R ddd-architecture-guidance ~/.agents/skills/
```

如果你的环境使用 `~/.codex/skills`，则使用这个路径：

```bash
mkdir -p ~/.codex/skills
cp -R ddd-architecture-guidance ~/.codex/skills/
```

### Claude Code 风格 skill 目录

用户级 skill：

```bash
mkdir -p ~/.claude/skills
cp -R ddd-architecture-guidance ~/.claude/skills/
```

项目级 skill：

```bash
mkdir -p .claude/skills
cp -R ddd-architecture-guidance .claude/skills/
```

Claude Code 不需要 `agents/openai.yaml`；可以保留，也可以删除。

## 使用示例

可以这样让 agent 使用：

```text
Use $ddd-architecture-guidance to review this Java service and tell me whether the aggregate boundaries are justified.
```

```text
Use $ddd-architecture-guidance to evaluate whether this .NET API should use CQRS or a simpler layered design.
```

```text
Use $ddd-architecture-guidance to propose an idiomatic Go package structure for this domain-heavy backend service.
```

```text
Use $ddd-architecture-guidance to decide whether DDD concepts are appropriate in this Flutter app with offline sync.
```

## 文件说明

- `SKILL.md`：触发描述和核心工作流
- `references/source-policy.md`：资料权威性分层、引用策略、Explicit Architecture 和 Clean Architecture 的谨慎使用说明
- `references/backend-guidance.md`：后端优先的语言和架构指导
- `agents/openai.yaml`：Codex/OpenAI 风格 skill 环境的可选 UI 元数据

## 架构单元测试

对于 Java/Kotlin 项目，这个 skill 把 [ArchUnit](https://www.archunit.org/) 及其 [Java 开源仓库](https://github.com/TNG/ArchUnit) 作为优先考虑的架构单元测试工具。它适合用来验证依赖方向、包边界、分层规则、adapter 隔离，以及基于 jMolecules 标注的架构约束。

对于 C#/.NET 项目，这个 skill 把 [ArchUnitNET](https://github.com/TNG/ArchUnitNET) 作为对应的架构单元测试工具。

ArchUnit 和 ArchUnitNET 是验证工具，不是架构模型本身的来源。架构模型仍然应该来自所选择的架构风格和资料来源策略。

## 设计原则

使用架构模式是为了保护业务语义和变化边界，而不是为了装饰代码结构。
