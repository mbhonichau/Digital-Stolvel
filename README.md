# Digital Stolvel

> **Digitizing Africa's oldest fintech — the rotating savings group — powered by MTN Mobile Money.**

---

## 🌍 Problem Statement

Across Africa, informal rotating savings and credit associations (ROSCAs) — known as *stokvels* in South Africa, *susus* in West Africa, *chamas* in East Africa, and *tontines* in Francophone Africa — represent one of the continent's most deeply embedded financial institutions, collectively mobilising an estimated **$180 billion annually** and serving hundreds of millions of people.

Despite this scale, these groups operate almost entirely outside the formal financial system: managed through cash, WhatsApp threads, and handwritten ledgers, with no accountability mechanisms, no audit trails, and no protection against fraud, disputes, or mismanaged funds. Group administrators bear the entire operational burden manually, while members remain in the dark about group finances, contribution status, and payout schedules.

MTN's Mobile Money (MoMo) network — active across **17+ African markets** with over **290 million subscribers** — has created the payment infrastructure necessary to bridge this gap at continental scale. Yet no dedicated platform exists to harness MoMo's reach specifically for the management of informal savings groups.

**Digital Stolvel fills this void:** a mobile-first, MoMo-native platform that formalises the ROSCA experience across Africa — digitizing group formation, member onboarding, contribution tracking, automated payout rotation, and cycle disbursements — transforming a centuries-old community savings tradition into a transparent, auditable, and inclusive fintech solution accessible to every African with a mobile phone, regardless of whether they hold a bank account.

---

## 🚀 Core Capabilities

| Feature | Description |
|---|---|
| **Group Management** | Create and configure savings groups with contribution frequency, amounts, and member rosters |
| **Invite & Onboarding** | Shareable invite codes and links for members to join a group |
| **Contribution Ledger** | Real-time live ledger tracking each member's payment status per cycle |
| **Cycle & Rotation Engine** | Automated rotation managing who receives the group pot each cycle |
| **MTN MoMo Payments** | Members contribute and receive disbursements via Mobile Money; payouts trigger automatically at end of each cycle |
| **Group History** | Full audit trail of past cycles, recipients, and amounts disbursed |
| **KYC Awareness** | Member KYC status tracking for financial compliance |
| **Security** | Role-based access control (admin/member) within groups |
| **Health & Observability** | Spring Actuator for metrics and health endpoints |

---

## 🛠️ Tech Stack

| Layer | Technology |
|---|---|
| **Frontend** | React 18 + TypeScript, Vite, Tailwind CSS |
| **Backend** | Spring Boot 3.4, Java 21, Spring Security, Spring Data JPA |
| **Database** | PostgreSQL (production), H2 (dev/test), Flyway migrations |
| **Payments** | MTN Mobile Money (MoMo) API |
| **State Management** | TanStack Query, Zustand |

---

## 📁 Project Structure

```
Digital-Stolvel/
├── frontend-React/     # React + TypeScript web application
│   └── src/
│       ├── pages/      # Route-level page components
│       ├── components/ # Reusable UI components
│       ├── hooks/      # TanStack Query data hooks
│       ├── store/      # Zustand global state
│       ├── api/        # Backend API client
│       └── types/      # TypeScript type definitions
└── backend-Java/       # Spring Boot REST API
    └── src/main/java/com/digitalstokvel/
        ├── group/        # Group & membership domain
        ├── contribution/ # Contribution tracking
        ├── cycle/        # Rotation cycle management
        ├── payout/       # Payout disbursement
        ├── member/       # Member & KYC management
        └── momo/         # MTN MoMo payment integration
```

---

## ⚙️ Getting Started

### Prerequisites
- Node.js 18+
- Java 21+
- PostgreSQL 15+
- MTN MoMo API credentials

### Frontend

```bash
cd frontend-React
cp .env.example .env
npm install
npm run dev
```

### Backend

```bash
cd backend-Java
cp .env.example .env.local
./mvnw spring-boot:run
```

The backend defaults to safe MoMo mock mode. To use the MTN sandbox, copy
`backend-Java/.env.example` to the gitignored `backend-Java/.env.local`, source
it in your shell, add the supplied credentials, and set `MOMO_MOCK_MODE=false`.
Pending contributions and payouts are polled every five seconds by default; no
public callback URL is required.

---

## 📜 License

This project is licensed under the terms of the [LICENSE](./LICENSE) file included in this repository.

---

> *Built to bring financial transparency, trust, and inclusion to Africa's most powerful grassroots savings tradition.*
