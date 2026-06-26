# Custom Security Detector — Unsafe `XMLDecoder` Usage

A hands-on build of a **static analysis security detector**: a rule that automatically scans Java code and flags a dangerous, exploitable mistake — without ever running the program.

Built as a detector for **[Find Security Bugs](https://find-sec-bugs.github.io/)** (the security plugin for **[SpotBugs](https://spotbugs.github.io/)**, a static analysis tool used on real Java codebases). Think of it as spell-check, but instead of underlining typos, it underlines security holes.

> **Note on scope:** This is a learning project. It re-implements the *pattern* of an existing Find Security Bugs detector under a separate bug type (`MY_XML_DECODER`) so I could understand, end to end, how these tools are actually built. It is **not** a new contribution to the upstream project and is not intended as a pull request.

---

## What it catches

The detector flags unsafe use of **`java.beans.XMLDecoder`** to parse XML.

If an application feeds untrusted XML into an `XMLDecoder`, an attacker can craft malicious input that causes the program to **execute arbitrary code** — a remote code execution vulnerability. This is a classic **deserialization of untrusted data** flaw, tracked as **[CWE-502](https://cwe.mitre.org/data/definitions/502.html)**, and it has been the root cause of serious real-world breaches.

When the detector finds this pattern, it reports the vulnerability and points to the **exact file and line** — the kind of automated check teams wire into a CI/CD pipeline so vulnerable code is caught before it ships.

---

## How it works

The interesting part: the detector doesn't read source code. It analyzes **compiled Java bytecode**.

SpotBugs feeds every bytecode instruction of the scanned program to the detector, one at a time. The detector watches that stream and fires only when it sees the precise instruction that constructs an `XMLDecoder`:

- the `INVOKESPECIAL` opcode (used to call a constructor),
- where the class being constructed is `java/beans/XMLDecoder`,
- and the method name is `<init>` (the bytecode name for a constructor).

When all three match, it reports a `MY_XML_DECODER` bug at that source line.

It was built **test-first (TDD)**: a deliberately vulnerable sample, a test that fails on purpose, then the detector written to make it pass — the same red-to-green loop used to develop this kind of tooling in practice.

---

## The pieces

| File | Role |
|------|------|
| [`MyXmlDecodeUtil.java`](findsecbugs-plugin/src/test/java/testcode/mydecoder/MyXmlDecodeUtil.java) | The "bait" — a small, deliberately vulnerable class that uses `XMLDecoder` |
| [`MyXmlDecoderDetectorTest.java`](findsecbugs-plugin/src/test/java/com/h3xstream/findsecbugs/MyXmlDecoderDetectorTest.java) | The test: asserts the detector reports `MY_XML_DECODER` on the vulnerable line (TestNG + Mockito) |
| [`MyXmlDecoderDetector.java`](findsecbugs-plugin/src/main/java/com/h3xstream/findsecbugs/MyXmlDecoderDetector.java) | The detector itself — the bytecode pattern-matching logic |
| `findbugs.xml` | Registers the detector and declares the `MY_XML_DECODER` bug type (search the file for `MY_XML_DECODER`) |
| `messages.xml` | Human-readable descriptions shown when the bug is reported (search for `MY_XML_DECODER`) |

---

## Running it

Requires **JDK 11** and **Maven** (the project targets Java 8 bytecode; newer JDKs like 17+ break the build).

Easiest path is to open the project in IntelliJ IDEA and run `MyXmlDecoderDetectorTest` directly. Or from the repository root:

```bash
mvn clean test
```

A passing run means the detector successfully spotted the `XMLDecoder` construction in the bait class and reported the bug on the correct line.

```
Total tests run: 1, Passes: 1, Failures: 0, Skips: 0
```

---

## What I took away

- How a static analyzer reads **bytecode** and pattern-matches on **opcodes** — something I'd only read about before building it.
- Why deserialization sinks like `XMLDecoder` are dangerous, and how a tool catches them automatically.
- The **test-driven** workflow for security tooling: prove the test fails for the right reason, then make it pass.
- Navigating and building a real open-source security project (Find Security Bugs / SpotBugs) from source.

---

## Credits

Built on top of **[Find Security Bugs](https://find-sec-bugs.github.io/)** by Philippe Arteau and contributors, which is itself a plugin for **[SpotBugs](https://spotbugs.github.io/)**. All credit for those projects goes to their authors; this repository is a personal learning fork.
