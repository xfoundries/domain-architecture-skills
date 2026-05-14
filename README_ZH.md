# DDD Architecture 指导

中文 ｜ [English](README.md)

---

面向业务领域系统的、重视资料来源层级的架构指导 skill，覆盖 Domain-Driven Design、Layered Architecture、Onion Architecture、Hexagonal Architecture / Ports and Adapters，以及 CQRS。

这个 skill 适用于支持 `SKILL.md` 结构的 AI 编程 agent，包括 Codex 风格的 skill 环境，以及 Claude Code 风格的本地 skills。

## 目的

这个 skill 用来帮助 AI agent 在设计、评审、重构、文档化或实现架构决策时，正确运用 Domain-Driven Design、Layered Architecture、Onion Architecture、Hexagonal Architecture / Ports and Adapters 以及 CQRS，同时避免把它们混成一个所谓的标准组合模型。

这个 skill 的主要实践参考是 [jMolecules](https://github.com/xmolecules/jmolecules)。jMolecules 是 Java 生态中用于在代码中表达 DDD building blocks 和架构风格的成熟开源项目。这个 skill 把 jMolecules 作为 Java/Kotlin 的强参考，也把它作为其它语言生态的概念参考，但仍然要求 agent 根据目标语言做生态化翻译，而不是把某个框架的结构复制到所有项目里。

在做架构论断时，它也优先参考原始资料或被广泛认可的资料。

这并不意味着削弱架构约束。如果一个项目明确选择了 Layered、Onion、Hexagonal / Ports and Adapters 或 CQRS，这个 skill 应该严格执行该架构的依赖方向、边界和职责规则。重点是把每条规则归属到正确的架构语境，而不是把所有规则都说成 DDD 本身的要求。

DDD 在这里被视为领域建模方法论，而不是这些架构的拥有者。Layered、Onion、Hexagonal / Ports and Adapters 和 CQRS 可以是在复杂业务系统中实现和保护领域模型的优秀方式，但一旦选择了某种架构，就应该遵循该架构自身的约束。

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

## Reference 组织方式

这个 skill 有意按照语言/生态和验证工具来组织实践指导，而不是按 `DDD.md`、`HEXAGONAL.md`、`CQRS.md` 这类架构标签拆分。

原因是，这个 skill 的目标不是把架构概念复述成固定模板，而是帮助 agent 基于资料来源做后端实践判断，并把这些判断翻译成符合 Java/Kotlin、C#/.NET、Go、Python，或有必要时客户端代码的生态习惯。

这些架构模式在实践中经常重叠。按生态组织示例更贴近真实实现选择，也能降低把 DDD、Layered、Onion、Hexagonal、CQRS、Event Sourcing 误解成一个强制组合结构的风险。

## 资料来源策略

这个 skill 把资料分为三层。

基础来源：

- Eric Evans 关于 Domain-Driven Design 的资料：https://www.domainlanguage.com/wp-content/uploads/2016/05/DDD_Reference_2015-03.pdf
- Alistair Cockburn 关于 Hexagonal Architecture / Ports and Adapters 的资料：https://alistair.cockburn.us/hexagonal-architecture/
- Martin Fowler 关于企业应用架构和 CQRS 的资料：https://martinfowler.com/ 和 https://martinfowler.com/bliki/CQRS.html
- Greg Young 关于 CQRS 的资料
- Jeffrey Palermo 关于 Onion Architecture 的资料：https://jeffreypalermo.com/2008/07/the-onion-architecture-part-1/
- Robert C. Martin 关于 Clean Architecture 的资料，但只谨慎用于依赖方向和独立性原则：https://blog.cleancoder.com/uncle-bob/2012/08/13/the-clean-architecture.html

广泛使用的实现指导：

- [jMolecules](https://github.com/xmolecules/jmolecules)
- [Microsoft Learn 的 .NET DDD-oriented microservice 指南](https://learn.microsoft.com/en-us/dotnet/architecture/microservices/microservice-ddd-cqrs-patterns/ddd-oriented-microservice)
- Microsoft Learn / Azure Architecture Center / .NET architecture guides
- [Spring Modulith](https://spring.io/projects/spring-modulith)
- [ArchUnit](https://www.archunit.org/) / [ArchUnit Java 仓库](https://github.com/TNG/ArchUnit)
- [ArchUnitNET](https://github.com/TNG/ArchUnitNET)
- microservices.io

带有个人观点的综合模型和示例：

- Herberto Graca 的 Explicit Architecture 系列文章
- 把多种架构风格组合在一起的示例仓库
- 规定固定目录结构或通用规则的博客文章

带有个人观点的综合模型可以作为参考，但不应该被当成权威标准。

Microsoft 的资料在这里被视为务实的实现参考，尤其适用于 .NET 微服务。它适合参考何时使用 DDD、何时简单 CRUD 就够、如何保持 domain entities 独立于 infrastructure、以及如何组织 .NET layers/projects 等取舍。但它不是 DDD、Hexagonal Architecture、Onion Architecture 或 CQRS 的原始定义来源。

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

### skills.sh / skills CLI

发布到 GitHub 后，用户可以通过 `skills` CLI 安装：

```bash
npx skills add youngledo/ddd-architecture-guidance
```

安装到指定 agent：

```bash
npx skills add youngledo/ddd-architecture-guidance -a claude-code
npx skills add youngledo/ddd-architecture-guidance -a codex
npx skills add youngledo/ddd-architecture-guidance -a cursor
npx skills add youngledo/ddd-architecture-guidance -a opencode
```

查看仓库中可用的 skills：

```bash
npx skills add youngledo/ddd-architecture-guidance --list
```

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

## 更新

通过 `skills` CLI 安装的 skill 不会被 Claude Code、Codex、Cursor 或 OpenCode 自动更新。仓库发布新版本后，用户需要执行：

```bash
npx skills update ddd-architecture-guidance
```

如果是全局安装：

```bash
npx skills update ddd-architecture-guidance -g
```

如果是项目级安装：

```bash
npx skills update ddd-architecture-guidance -p
```

## 发布说明

这个仓库适合作为单 skill GitHub 仓库发布，因为 `SKILL.md` 位于仓库根目录。

这个 skill 已经可以通过 GitHub 直接安装：`npx skills add youngledo/ddd-architecture-guidance`。skills.sh 的详情页和 badge 在仓库索引期间可能表现不稳定，所以本文档以直接 GitHub 安装命令作为准确信息来源。

建议添加的 GitHub topics：

```text
agent-skills
skill-md
skills-sh
claude-code
codex
cursor
opencode
ddd
domain-driven-design
hexagonal-architecture
cqrs
architecture-testing
jmolecules
archunit
```

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
- `references/source-policy.md`：资料权威性分层、引用策略、Explicit Architecture 的谨慎使用说明，以及 Clean Architecture 作为二次综合资料的谨慎参考说明
- `references/architecture-constraints.md`：按架构风格组织的依赖、调用路径和边界约束
- `references/backend-guidance.md`：后端优先的语言和架构指导
- `references/examples-java-kotlin.md`：面向 jMolecules 的 Java/Kotlin 示例
- `references/examples-csharp-dotnet.md`：C#/.NET 示例
- `references/examples-go.md`：Go 示例
- `references/examples-python.md`：Python 示例
- `references/architecture-testing.md`：ArchUnit、ArchUnitNET 和轻量架构测试示例
- `agents/openai.yaml`：Codex/OpenAI 风格 skill 环境的可选 UI 元数据

## 架构单元测试

对于 Java/Kotlin 项目，这个 skill 把 [ArchUnit](https://www.archunit.org/) 及其 [Java 开源仓库](https://github.com/TNG/ArchUnit) 作为优先考虑的架构单元测试工具。它适合用来验证依赖方向、包边界、分层规则、adapter 隔离，以及基于 jMolecules 标注的架构约束。

对于 C#/.NET 项目，这个 skill 把 [ArchUnitNET](https://github.com/TNG/ArchUnitNET) 作为对应的架构单元测试工具。

ArchUnit 和 ArchUnitNET 是验证工具，不是架构模型本身的来源。架构模型仍然应该来自所选择的架构风格和资料来源策略。

## 设计原则

使用架构模式是为了保护业务语义和变化边界，而不是为了装饰代码结构。
