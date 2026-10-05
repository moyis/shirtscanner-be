# Public API has no authentication (by decision)

Date: 2026-10-05
Status: Accepted — no code change

## Finding

Aikido's AI pentest reported `POST /v1/providers` as an unauthenticated route that
persists provider state, in `infrastructure/controllers/ProvidersController.kt`.

## Assessment

The finding is a false positive. `ProvidersController` exposes only
`@GetMapping findAll()`. There is no `POST` handler anywhere in the codebase, and
no Spring Security dependency on the classpath — there is no filter chain that
could be bypassed, because there is no auth to bypass.

## Decision

The service ships without authentication. That is intentional, not an oversight,
and it is not something to close by adding Spring Security.

Consequences accepted:

- Every route is publicly reachable. This bounds what any route may do: reads of
  public provider metadata only. No route may accept a write, trigger outbound
  network work on demand, or return provider credentials.
- Aikido will keep reporting this shape of finding whenever it infers a route from
  `@RequestMapping` plus naming. Expect the report; it does not indicate a change.
- If a write route is ever added, authentication becomes a precondition of that
  change, not a follow-up.

## Related real gaps (tracked separately, not by this finding)

- `deploy/Dockerfile` ran as root. Fixed in `fix/aikido-ci-privilege-escalation`.
