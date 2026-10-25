# hotel-loyalty Unit Testing Strategy and Standards

> Version: 1.3 (English) ｜ Scope: all backend modules of `hotel-loyalty` ｜ Based on: Alibaba Java Development Manual (Taishan Edition) unit testing chapter + Spring Boot 4 official testing system

---

## 1. Purpose and Scope

This document defines the unit testing strategy, layer boundaries, writing rules, and priority plan for the hotel-loyalty project. All team members must follow it when adding tests.

**In scope**: all packages under `src/main/java/com/hotel/hotel_loyalty` (controller / service / mapper / payment / order / security / messaging / common).

**Out of scope**: JSP page visual checks (covered by GUI prototype HTML files), performance testing, deployment checks.

> **Before you use this document**: The dependency versions, annotation package names, class names, and APIs in this document were checked when it was written. But frameworks change. Before writing tests, **always check the real dependencies in `pom.xml` and the imports that compile in the source code**. If the document does not match the real code, ask first. Do not blindly copy from the document.

---

## 2. Core Principles (aligned with Alibaba rules)

| Principle | Requirement | How we apply it in this project |
|-----------|-------------|----------------------------------|
| **A — Automatic** | Tests must run fully automatically. No manual checking. | Use AssertJ for all assertions. No `System.out.println` for human reading. `mvn test` must pass all green in one run. |
| **I — Independent** | Tests must not call each other or depend on run order. | Each test method builds its own data (`@BeforeEach` reset). No `@Order`. Mapper tests use H2 in-memory DB + `@Sql` or `@Transactional` rollback (except rollback-semantics tests, see §5.4(b)). |
| **R — Repeatable** | Same result in any environment, any number of runs. | No external services (real DB / Artemis Broker). H2 in-memory DB is rebuilt on every start (schema.sql + data.sql). JMS tests use embedded Artemis (`spring.artemis.mode=embedded` in `application-test.yaml`). |
| **B — Border** | Must cover boundary values. | Balance exactly equals product price, stock exactly 1, points = 0, negative cash. |
| **C — Correct** | Must cover correct input. | All three payment types (Type 1/2/3) with valid combinations. |
| **D — Design** | Must align with design documents (UML sequence/state diagrams). | UC3 redeem flow step by step: find available products → choose payment type → validate → CAS deduct points → CAS deduct stock → create order and change status → record flow (see `redeem_points_for_products.svg`). Order status flow: PENDING_PAYMENT→PAID→COMPLETED, PENDING_PAYMENT→CANCELLED (see `order_state_diagram_1.0.svg`). |
| **E — Error** | Must cover wrong input and exception paths. | Not enough balance, sold out, illegal status change, IDOR access violation, concurrent CAS failure. |

**Coverage goals**: overall statement coverage ≥ 70%. Core modules (`payment`, `order`, `service/impl`, `mapper`) core method coverage ≥ 90%.

---

## 3. Testing Strategy Overview

We use the **testing pyramid** model. Layers are split by "distance from business core". Ratio is about **70% / 20% / 10%**:

```
        ┌──────────────┐
        │  End-to-end   │  10%  @SpringBootTest + H2
        │  integration  │      verify transaction rollback / RBAC / messaging
        ├──────────────┤
        │  Slice tests  │  20%  @WebMvcTest / @MybatisTest
        │  (Web/DB)     │      verify routing / SQL, no full context
        ├──────────────┤
        │  Pure unit    │  70%  JUnit 5 + Mockito, no Spring context
        │(strategy/state)│     runs in milliseconds, coverage can reach 100%
        └──────────────┘
```

> **Why this ratio**: Higher layers are slower and more expensive. Pure unit tests run in milliseconds with no Spring startup cost, so we can write many of them. `@SpringBootTest` takes seconds to start the full context, so we save it for paths that truly need to verify transactions, concurrency, or security. 70/20/10 is not a hard quota. It is a direction: "test at the lowest layer possible."

**Layer boundary rules**:

| Class under test | Test type | Spring context | Dependency handling |
|------------------|-----------|----------------|---------------------|
| `payment/*Strategy`, `payment/PaymentStrategyFactory` | Pure unit | None | All Mappers `@Mock` |
| `order/*State`, `order/OrderContext` | Pure unit | None | No external deps (pure state change) |
| `service/impl/*` (validation/orchestration logic) | Pure unit | None | All Mappers / Factory `@Mock` |
| `service/impl/GuestPointsServiceImpl#redeem` (transaction + CAS) | Integration | `@SpringBootTest` | Real H2 + real Mappers |
| `mapper/*` | Slice | `@MybatisTest` | Real H2 in-memory DB |
| `controller/*` | Slice | `@WebMvcTest` | Service `@MockitoBean` |
| `security/*` | Integration | `@SpringBootTest` + MockMvc | Real Security chain |
| `messaging/*` | Integration | `@SpringBootTest` + embedded Artemis | `spring.artemis.mode=embedded` (Boot 4 has no JMS slice annotation) |

---

## 4. Tech Stack and Current Dependencies

### 4.1 Already available (declared in pom.xml, no new deps needed)

`spring-boot-starter-webmvc-test` already brings in JUnit 5 / Mockito / AssertJ / MockMvc / Spring Test. `mybatis-spring-boot-starter-test` 4.0.1 (for Boot 4) provides `@MybatisTest`. `spring-boot-starter-artemis-test` is a combined package of the artemis starter + test starter; it **does not provide a JMS slice annotation** (Boot 4 has no `@JmsTest`; JMS tests use `@SpringBootTest` + embedded Artemis). `h2` (runtime) is the in-memory DB. **No new test dependencies are needed in pom.xml.**

### 4.2 To confirm / add

- `h2` currently has scope `runtime`. `@MybatisTest` can replace it with an in-memory DB by default. But if you want to declare `jdbc:h2:mem:testdb` in the test config, create `src/test/resources/application-test.yaml` (see §6.5).
- If integration tests need JMS but should not connect to a real Broker, set `spring.artemis.mode=embedded` in `application-test.yaml` (see §4.3), or disable JMS auto-config in the test config.

### 4.3 Test config file rules

In `src/test/resources/application-test.yaml`:

```yaml
spring:
  datasource:
    url: jdbc:h2:mem:testdb;DB_CLOSE_DELAY=-1;MODE=MySQL
    driver-class-name: org.h2.Driver
  sql:
    init:
      schema-locations: classpath:schema.sql
      data-locations: classpath:data.sql
  artemis:
    mode: embedded   # use embedded Broker, no external connection
```

Integration test classes use `@ActiveProfiles("test")` to activate it.

---

## 5. Layer-by-Layer Test Rules

### 5.1 Pure unit tests — payment package (Strategy pattern)

**Classes under test**: `PointsOnlyStrategy`, `PointsPlusCashStrategy`, `PointsPlusVoucherStrategy`, `PaymentStrategyFactory`

**Test base**: `@ExtendWith(MockitoExtension.class)`. Mappers use `@Mock`. The class under test uses `@InjectMocks`. No Spring startup.

**Required test cases**:

| Case | Type | Assertion focus |
|------|------|-----------------|
| Type 1: points = full price, no cash/voucher, balance enough | C | `PaymentResult.success() == true` |
| Type 1: points < full price | E | Reject. Error says not enough points. |
| Type 1: carries cash or voucher | E | Reject. Points-only does not allow mixed payment. |
| Type 1: balance exactly equals product price | B | Pass (boundary) |
| Type 2: points >= minPoints and < full price, cash > 0, balance enough | C | Pass |
| Type 2: points < minPoints | E | Reject |
| Type 2: cash = 0 or negative | B/E | Reject |
| Type 2: points = full price (should go to Type 1) | E | Reject |
| Type 3: same as Type 2, voucher replaces cash | C/E | Same logic as above |
| Factory: Type 1/2/3 each returns the right strategy instance | C | `instanceof` assertion |
| Factory: undefined type value | E | Throws expected exception |

**Example skeleton** (only shows Given-When-Then; **real fields and constructor style depend on source code** — `PaymentRequest` is a `@RequiredArgsConstructor` immutable class with no builder; fields are `uid/pid/pmid` (String) + `pointsUsed/cashUsed/...`):

```java
@ExtendWith(MockitoExtension.class)
class PointsOnlyStrategyTest {
    @Mock private UserMapper userMapper;
    @InjectMocks private PointsOnlyStrategy strategy;

    @Test
    void validate_whenPointsEqualsPriceAndBalanceSufficient_shouldSucceed() {
        // given: build PaymentRequest with its real constructor (check the source)
        PaymentRequest req = new PaymentRequest(/* uid, pid, pmid, pointsUsed, cashUsed, ... */);
        when(userMapper.selectById("1")).thenReturn(userWithBalance(200));
        // when
        PaymentResult result = strategy.validate(req);
        // then
        assertThat(result.isSuccess()).isTrue();
    }
}
```

### 5.2 Pure unit tests — order package (State pattern)

**Classes under test**: `OrderContext`, `PendingPaymentState`, `PaidState`, `CancelledState`, `CompletedState`

**Test base**: pure JUnit 5, no Mocks. State classes are self-contained. Use `OrderContext.fromDbStatus()` to build the initial state.

**Required test cases** (aligned with 4 states + 5 transitions in `order_state_diagram_1.0.svg`):

| Initial state | Action | Expected result |
|---------------|--------|-----------------|
| PENDING_PAYMENT | pay() | State -> PAID |
| PENDING_PAYMENT | cancel() | State -> CANCELLED |
| PENDING_PAYMENT | complete() | Throws `IllegalStateException` |
| PAID | complete() | State -> COMPLETED |
| PAID | pay() | Throws `IllegalStateException` (double pay) |
| CANCELLED | pay() / complete() / cancel() | All throw exceptions (final state) |
| COMPLETED | pay() / complete() / cancel() | All throw exceptions (final state) |
| `fromDbStatus(0/1/2/3)` | — | Rebuilds the matching state instance |

> **Deferred area note**: `PaidState.cancel()` involves conflict #5 (refund semantics when cancelling a PAID order). The refund side effect is not implemented yet. Tests only assert state changes to CANCELLED. **Do not assert refund or stock restore.** Add comment: `// TODO add refund assertions after conflict #5 is resolved`.

### 5.3 Persistence slice tests — mapper package (@MybatisTest)

**Classes under test**: all 6 Mappers.

**Test base**: `@MybatisTest` + `@AutoConfigureTestDatabase(replace = NONE)` + `@ActiveProfiles("test")`. `replace = NONE` means Spring Boot will **not** replace the datasource with its built-in embedded DB. Instead it uses the H2 configured in `application-test.yaml` (`jdbc:h2:mem:testdb;MODE=MySQL`, aligned with the production MySQL dialect). `spring.sql.init` runs `schema.sql` + `data.sql`. Each test method rolls back automatically with `@Transactional` (on by default in `@MybatisTest`).

> **Why Mappers must run real SQL, not Mockito**: The concurrency correctness of this project relies entirely on the WHERE conditions of CAS-style UPDATEs (`WHERE points >= ?`, `WHERE remaining_stock > 0`, `WHERE status = ?`). Mocking a Mapper only verifies "it was called". **A wrong WHERE condition cannot be caught by a mock.** Only real SQL hitting a real DB can verify "condition met returns 1, condition not met returns 0".

**Key: CAS optimistic lock verification** (foundation of concurrency correctness in this project):

| Method under test | Case | Assertion focus |
|-------------------|------|-----------------|
| `UserMapper.compareAndUpdatePoint` | Balance enough | Returns 1, balance deducted correctly |
| `UserMapper.compareAndUpdatePoint` | Balance not enough | Returns 0, balance unchanged |
| `ProductMapper.deductStock` | Stock >= 1 | Returns 1, `remaining_stock` minus 1 |
| `ProductMapper.deductStock` | Stock = 0 | Returns 0, stock unchanged |
| `OrderMapper.compareAndUpdateStatus` | Current status matches | Returns 1, status updated |
| `OrderMapper.compareAndUpdateStatus` | Current status changed by another thread | Returns 0, status unchanged |
| `OrderMapper.countActiveByPid` | Active orders exist | Returns > 0 (UC6 delete precondition) |
| `OrderMapper.countActiveByPid` | No active orders | Returns 0 |
| `ProductMapper.selectAll` | Sold-out product exists (stock=0) | **Sold-out products must not be returned** ⚠️ |

> ⚠️ **Known design gap**: The current `ProductMapper.selectAll()` is missing `WHERE remaining_stock > 0`. This does not match the return condition `productList (remainingStock > 0)` in steps 1-2 of the UC3 sequence diagram. **Fix the SQL first, then make the test above pass.** This is exactly what "tests align with design documents (Principle D)" means.

### 5.4 Business service layer tests — service/impl package

**Two levels**:

**(a) Pure orchestration logic -> Mockito unit tests** (fast, covers branches):

Methods in `GuestPointsServiceImpl` other than `redeem()` (query mall, query points page, query confirm page data), plus validation/query logic in `PointsRuleServiceImpl`, `ProductManagementServiceImpl`, `PointsAdjustmentServiceImpl`.

- All Mappers `@Mock`. `PaymentStrategyFactory` is injected as the **real instance** by default (it is stateless and only looks up a Map by type; mocking it is pointless). Only `@Mock` it when you need to force a specific type to return a specific strategy.
- Cover: normal return, record-not-found exception, UC6 delete precondition `countActiveByPid > 0` rejects deletion, etc.

**(b) `redeem()` core redeem flow -> integration tests** (slow but real, verifies transactions and optimistic locks):

**Test base**: `@SpringBootTest` + `@ActiveProfiles("test")`.

> **⚠️ The test-level `@Transactional` trap**: Cases that verify "data is restored after transaction rollback" (rows marked ⚠️ below) **must not** add `@Transactional` on the test method or class. The test transaction would merge with the transaction of the method under test into one physical transaction. When the method under test throws, the outer test transaction also rolls back. The "data restored after failure" assertion is always true, so the check is lost. These cases use `@Sql` to clean data instead. **Note: `@Sql` must run both before and after the test** (`executionPhase = ExecutionPhase.BEFORE_TEST_METHOD` to clean leftovers + `AFTER_TEST_METHOD` to clean itself). Otherwise data left by one case pollutes the next, breaking Principle I.

> **Why redeem() cannot be a Mockito unit test only**: `redeem()` is a four-step CAS chain: "deduct points -> deduct stock -> change status -> record flow", held together by `@Transactional` so that any failed step rolls back everything. With Mockito mocking the Mappers: (1) the real transaction rollback does not exist, so you cannot verify "points restored when status change fails after points were deducted"; (2) the CAS WHERE conditions are bypassed by stubs, so concurrency correctness cannot be tested. Only real H2 + real Mappers + real transactions can verify "zero side effects in the DB after failure".

**Required test cases** (aligned with the 14 messages in the UC3 sequence diagram):

| Case | Verification point |
|------|--------------------|
| Type 1 normal redeem | Points deducted + stock deducted + order PAID + REDEEM negative flow record created |
| Not enough balance ⚠️ | Throws business exception, **balance/stock/order all unchanged** (transaction rollback) |
| Not enough stock ⚠️ | Throws business exception, no side effects |
| CAS point deduction failed (simulated concurrency) ⚠️ | `compareAndUpdatePoint` returns 0 -> exception and rollback, stock not deducted |
| CAS status change failed ⚠️ | `compareAndUpdateStatus` returns 0 -> exception and rollback, **points were deducted but restored by rollback** |
| Invalid payment type ⚠️ | Throws exception, no side effects |

> ⚠️ = cases that verify rollback semantics. Do not add test-level `@Transactional`. Use `@Sql` to clean (see the trap note above).

> **How to simulate CAS failure in integration tests**: First call the Mapper directly inside the test to deduct points or change status ahead of time, then call `redeem()` so its CAS condition is not met. The ahead-of-time change must be truly committed to the DB — without test-level `@Transactional`, each Mapper call commits on its own, which naturally satisfies this. If the method under test "cannot see" the ahead-of-time change, first check whether it was accidentally wrapped in an uncommitted transaction.

### 5.5 Web layer slice tests — controller package (@WebMvcTest)

**Test base**: `@WebMvcTest(GuestJspController.class)` + `@MockitoBean GuestPointsService` (in Boot 4, `@MockBean` is removed; use `org.springframework.test.context.bean.override.mockito.MockitoBean`. The `@WebMvcTest` package name also changed to `org.springframework.boot.webmvc.test.autoconfigure`). When Spring Security is needed, add `@Import(SecurityConfig.class)` or use `@WithMockUser`.

**Required test cases**:

| Controller | Case | Assertion focus |
|------------|------|-----------------|
| `GuestJspController` | GET points-mall normal | Returns view name `guest/points_mall`, model contains `mall` |
| `GuestJspController` | GET confirm-payment normal | Model contains product + payment type list |
| `GuestJspController` | **IDOR: URL uid ≠ logged-in identity** | Redirect/reject (`checkOwnership` works) |
| `GuestJspController` | POST redemptions form missing fields | Validation fails, flash message shown |
| `GuestJspController` | POST redemptions business exception (not enough balance) | Flash message shown, no 500 |
| `ProductManagementController` | POST without login | 401/redirect |
| `ProductManagementController` | Normal user accesses admin route | 403 |
| `GlobalExceptionHandler` | Service throws business exception | Unified `Result` error structure |

> IDOR tests are the core security verification point of this project and **must be covered**: build a request with `uid=2` but log in as `uid=1`, and assert that uid=2 data is not returned.

### 5.6 Security integration tests — security package

**Test base**: `@SpringBootTest` + `@AutoConfigureMockMvc`. Do not mock the Security chain.

| Case | Assertion focus |
|------|-----------------|
| Access `/guest/**` without login | 302 redirect to login page |
| Access `/admin/**` without login | 302/401 |
| Guest role accesses `/admin/**` | 403 |
| Admin role accesses `/admin/**` | 200 |
| Login with correct username and password | `LoginSuccessHandler` redirects to the right portal |
| Login with wrong password | `LoginFailureHandler` shows error |
| Disabled user logs in | `StatusCheckHandler` rejects |

### 5.7 Messaging tests — messaging package

**Test base**: `@SpringBootTest` + `@ActiveProfiles("test")`. The embedded Artemis Broker starts via `spring.artemis.mode=embedded` in `application-test.yaml`.

> **Why no slice annotation**: Spring Boot 4 does not provide a JMS/Artemis test slice (it is not in the official test-modules list; `spring-boot-starter-artemis-test` is only a starter merge package). Message send/receive needs the full ConnectionFactory + listener container + consumer chain, which is exactly the case that "must load the full context to verify". So it belongs to the integration layer. Assert async consumption results with Awaitility polling (no `Thread.sleep`, see §6.6 red line #4).

| Case | Assertion focus |
|------|-----------------|
| Producer sends -> Consumer receives | Message handled by `PointsAdjustmentConsumer`; Awaitility polling asserts points adjustment took effect |
| Message body deserialization | `PointsAdjustmentMessage` fields complete |
| `RewardDistributionListener` event triggered | Points reward logic runs |

---

## 6. Writing Rules

### 6.1 Naming rules (aligned with Alibaba rules)

| Item | Rule | Example |
|------|------|---------|
| Test class | `ClassUnderTest + Test`, same package as the class under test | `PointsOnlyStrategyTest` |
| Test method | `test + method under test + scenario + expected` (camelCase) | `testValidate_whenInsufficientBalance_shouldReject` |
| Test directory | `src/test/java`, package path same as `src/main/java` | `com.hotel.hotel_loyalty.payment` |
| Test config | `src/test/resources/application-test.yaml` | — |

### 6.2 Method structure: Given-When-Then

Each test method body is split into three parts with comments. Each part does one thing:

```java
@Test
void testDeductStock_whenStockIsZero_shouldReturnZero() {
    // given: insert a product with stock 0
    ProductDO soldOut = insertProduct("test product", 0);
    // when: try to deduct stock
    int affected = productMapper.deductStock(soldOut.getId());
    // then: CAS fails, stock unchanged
    assertThat(affected).isZero();
    assertThat(productMapper.selectById(soldOut.getId()).getRemainingStock()).isZero();
}
```

**Forbidden**: verifying multiple unrelated scenarios in one test method. One method verifies one behavior.

### 6.3 Assertion rules

| Rule | Description |
|------|-------------|
| Always use AssertJ | `assertThat(x).isEqualTo(y)`. No raw JUnit `assertEquals` (hard to read). |
| Precise assertions | Do not just assert "no exception thrown". You must assert return value / state / side effects. |
| Exception assertions | Use `assertThatThrownBy(...).isInstanceOf(...).hasMessageContaining(...)`. |
| Collection assertions | Use `assertThat(list).extracting(Xxx::getName).containsExactly(...)`. |
| Optimistic lock assertions | CAS methods must assert **both the return value (0 or 1) and that data is unchanged**. Neither can be missing. |

### 6.4 Mock rules

| Rule | Description |
|------|-------------|
| Only mock dependencies below the same layer | Service tests only mock Mappers. Do not mock methods of the class under test itself. |
| Use `lenient()` only when necessary | Prefer precise stubs. An unused stub triggering `UnnecessaryStubbingException` is a good thing — it tells you "the code under test no longer calls this dependency; the stub is stale". Using `lenient()` freely turns off this alarm. |
| Verify side effects with `verify` | `verify(userMapper).compareAndUpdatePoint(...)` confirms the call happened. |
| No mocks in integration tests | Under `@SpringBootTest`, Mappers must use real H2. Otherwise the test loses its meaning. |

### 6.5 Test data rules

| Rule | Description |
|------|-------------|
| Build your own data; do not reuse data.sql business data | Seed data in `data.sql` is only for startup demo. Tests insert their own data with `@Sql` or Mappers. |
| Name test data with `test_` prefix or a clear marker | e.g. `test_user_001`, to avoid mixing with seed data. |
| Use `BigDecimal` for money/points | No `new BigDecimal(0.1)` (precision problem). Use `BigDecimal.valueOf(0.1)`. |
| Use fixed time values or inject a `Clock` | Avoid random failures caused by system time. |
| `@Transactional` test methods auto rollback at the end | Keeps the DB clean, meets Principle R. Exception: rollback-semantics cases (see §5.4(b)) use `@Sql` cleanup before and after instead. |

### 6.6 Forbidden items (red lines)

| # | Forbidden | Why |
|---|-----------|-----|
| 1 | `System.out.println` / `printStackTrace` debug output | Human reading breaks Principle A. Nobody reads output on CI. It equals no assertion. |
| 2 | Tests calling each other or depending on run order (`@Order`) | Change the order and everything goes red, breaking Principle I. When it fails, you cannot tell which case broke. |
| 3 | Depending on external environments (real MySQL / Artemis / network) | If the external service is down, everything goes red, breaking Principle R. CI environments often do not have these services. |
| 4 | `Thread.sleep` to wait for async results | Too short -> random failures. Too long -> slow build. Use Awaitility polling. Fast and stable. |
| 5 | `@Disabled` to skip a failing test without a reason | A skipped test rots forever. You must note the reason + a recovery plan (e.g. "restore after conflict #5 is resolved"). |
| 6 | Changing production code logic to "make the test pass" | Tests verify production code. Changing it the other way around is cheating. The right fix is to fix the code bug or fix the test expectation. |

---

## 7. Alibaba Rules Alignment Checklist

> The items below come from Section 9 of Chapter 1 in the Alibaba Java Development Manual (Taishan Edition), mapped to this project one by one.

| Rule item | How we apply it |
|-----------|-----------------|
| [Mandatory] Test class in the same package as the class under test, under `src/test/java` | §6.1 naming rules |
| [Mandatory] Test methods must return void, take no args, and be non-static | JUnit 5 `@Test` methods meet this naturally |
| [Mandatory] Unit tests must guarantee AIR (Automatic/Independent/Repeatable) | §2 core principles |
| [Recommended] Unit tests should guarantee BCDE coverage | §2 core principles + case tables per module |
| [Mandatory] No `System.out` in unit tests | §6.6 red line #1 |
| [Mandatory] Unit tests have the same quality as production code; no `if/switch/for` branch logic in tests | Test methods stay linear in three parts; cover branches with separate methods |
| [Mandatory] DB unit tests must not depend on a real environment and must be repeatable | H2 in-memory DB + `@Transactional` rollback (§4.3/§5.3; exception: rollback-semantics cases, see §5.4(b)) |
| [Recommended] Statement coverage 70%+, core modules 100% | §2 coverage goals (core methods 90%+, **intentionally lower than the rule's 100%**: core methods contain defensive exception branches and concurrent failure paths. Forcing 100% produces many low-value assertions that only cover catch blocks.) |
| [Mandatory] Reuse helper methods to build test data | §6.5 data rules + test base class provides builder helper methods |

---

## 8. Priority and Implementation Plan

Sorted by "business value x implementation cost", delivered in four batches:

### Batch 1 (P0 — core logic, pure unit, done within half a day)

| Order | Test class | Type | Estimated cases |
|-------|------------|------|-----------------|
| 1 | `PointsOnlyStrategyTest` | Pure unit | 5 |
| 2 | `PointsPlusCashStrategyTest` | Pure unit | 5 |
| 3 | `PointsPlusVoucherStrategyTest` | Pure unit | 5 |
| 4 | `PaymentStrategyFactoryTest` | Pure unit | 4 |
| 5 | `OrderContextTest` (includes 4 state classes) | Pure unit | 12 |

> Reason: Strategy and State are the core design-pattern showpieces of this project (they map to course grading points). No external deps, so results come fastest.

### Batch 2 (P1 — data correctness, Mapper slices, selectAll fix needed first)

| Order | Test class | Type | Estimated cases |
|-------|------------|------|-----------------|
| 6 | `ProductMapperTest` (includes selectAll stock filter) | @MybatisTest | 6 |
| 7 | `UserMapperTest` (CAS point deduction) | @MybatisTest | 4 |
| 8 | `OrderMapperTest` (CAS status + countActive) | @MybatisTest | 5 |

> Precondition: fix `ProductMapper.selectAll()` to add `WHERE remaining_stock > 0` (see §5.3 warning).

### Batch 3 (P2 — business orchestration and Web layer)

| Order | Test class | Type | Estimated cases |
|-------|------------|------|-----------------|
| 9 | `GuestPointsServiceImplTest` (query/validation part, Mockito) | Pure unit | 6 |
| 10 | `GuestPointsServiceImplIntegrationTest` (full redeem chain) | Integration | 6 |
| 11 | `GuestJspControllerTest` (includes IDOR) | @WebMvcTest | 6 |
| 12 | `ProductManagementServiceImplTest` | Pure unit | 5 |

### Batch 4 (P3 — security and messaging, if time allows)

| Order | Test class | Type | Estimated cases |
|-------|------------|------|-----------------|
| 13 | `SecurityIntegrationTest` (RBAC + login flow) | Integration | 7 |
| 14 | `PointsAdjustmentMessagingTest` | Integration | 3 |

---

## 9. Change Log

| Version | Date | Change |
|---------|------|--------|
| 1.0 | 2026-10-24 | First version: strategy overview, layer rules, writing rules, priority plan, case matrix |
| 1.1 | 2026-10-24 | Trimmed: removed the appendix case matrix that duplicated §5; added Why notes for layer choices, red lines, and Mock rules |
| 1.2 | 2026-10-24 | Fixed Boot 4 factual errors: `@MockBean`->`@MockitoBean` (removed in Boot 4); removed non-existent `@JmsTest` (Boot 4 has no JMS slice; use `@SpringBootTest` + embedded Artemis); fixed `replace=NONE` meaning in §5.3, the test-level `@Transactional` rollback trap in §5.4(b), and the real Factory instance default in §5.4(a); removed internal notes in §7 and explained the intentional coverage adjustment; turned the svg steps into text for Principle D in §2 |
| 1.3 | 2026-10-24 | Internal alignment: cross-references from §2/§6.5/§7 to §5.4(b) for rollback exceptions; added "security/messaging" to the pyramid end-to-end layer in §3; three batches -> four batches in §8; added `@Sql` before/after cleanup and CAS ahead-of-time change independent-commit notes in §5.4(b); added precondition note in §1 (check pom/import first); marked the example in §5.1 as illustrative and noted `PaymentRequest` has no builder |
| 1.3 (EN) | 2026-10-24 | English translation of v1.3, using simple English |