# Testing MTN MoMo with Postman

This guide tests the MTN MoMo API directly first, then connects the same
credentials to the Stokvel backend. Do not add API keys or access tokens to
source code, committed Postman files, screenshots, or chat messages.

## 1. Import and configure the supplied Postman files

1. In Postman, select **Import**.
2. Import the supplied **MoMo Open APIs Production SA** collection and its
   **Production SA** environment.
3. Select the imported environment from Postman's environment picker.
4. In **Environments**, change these values for the South African integration:

| Variable | Value |
| --- | --- |
| `base_url` | `https://proxy.momoapi.mtn.com` |
| `Target_Environment` | `mtnsouthafrica` |
| `Currency` | `ZAR` |
| `api_user` | Your MTN-issued API user |
| `api_key` | Your MTN-issued API key |
| `Collection_Subscription-Key` | Your Collections subscription key |
| `Disbursement_Subscription-Key` | Your Disbursement subscription key |
| `MSISDN` | A permitted South African test MSISDN, digits only |

Keep API keys and tokens in **Current value** only. Leave **Initial value**
empty so a shared/exported environment cannot expose them. The supplied
environment contains legacy Uganda values; do not use its `mtnuganda`, `UGX`,
or old subscription-key values for this project.

## 2. Get a Collections access token

In the collection, open **Authorization → Generate access_token** and click
**Send**.

The request uses Basic Auth automatically:

- username: `{{api_user}}`
- password: `{{api_key}}`
- subscription key: `{{Collection_Subscription-Key}}`

A successful response is `200 OK` and contains `access_token` and
`expires_in`. The collection saves these to `Access_Token` and
`Access_Token_Expiry`. Reuse the token until it expires; do not create one for
every request.

## 3. Test a contribution (Request to Pay)

Open **Get Paid → Request to Pay → Request To Pay** and click **Send**. The
collection generates a fresh UUID in `Request_ID_Debit` before each request.

Confirm the request has these values:

```json
{
  "amount": "5",
  "currency": "ZAR",
  "externalId": "your-internal-contribution-id",
  "payer": {
    "partyIdType": "MSISDN",
    "partyId": "27XXXXXXXXX"
  },
  "payerMessage": "Stokvel contribution",
  "payeeNote": "Stokvel contribution"
}
```

Expected result: `202 Accepted`. Save the UUID from `X-Reference-Id`; it is
the transaction reference used to look up the result.

## 4. Check the contribution result

Open **Get Paid → Payment Status** in the imported collection. Set its request
reference to `{{Request_ID_Debit}}`, then click **Send**.

Expected result: `200 OK` with a status such as `PENDING`, `SUCCESSFUL`, or
`FAILED`. Poll while the result remains `PENDING`.

## 5. Test a payout (Disbursement)

Use the collection's Disbursement transfer request. Ensure it uses:

- `Ocp-Apim-Subscription-Key: {{Disbursement_Subscription-Key}}`
- a separate Disbursement access token
- a fresh UUID for `X-Reference-Id`
- `currency: "ZAR"`
- a `payee` object with `partyIdType: "MSISDN"`

The call should return `202 Accepted`. Check its transfer-status request using
the same reference ID until it reaches `SUCCESSFUL` or `FAILED`.

## 6. Connect the Stokvel backend

Create `backend-Java/.env.local` from `.env.example`, populate it from the same
Postman Current values, and set `MOMO_MOCK_MODE=false`. Source those variables
before starting Spring Boot.

```text
MOMO_API_USER=<Postman api_user>
MOMO_API_KEY=<Postman api_key>
MOMO_COLLECTIONS_SUBSCRIPTION_KEY=<Postman Collection_Subscription-Key>
MOMO_DISBURSEMENT_SUBSCRIPTION_KEY=<Postman Disbursement_Subscription-Key>
MOMO_BASE_URL=https://proxy.momoapi.mtn.com
MOMO_TARGET_ENV=mtnsouthafrica
MOMO_CURRENCY=ZAR
MOMO_MOCK_MODE=false
```

The backend automatically obtains and caches the two product tokens. Its
scheduled poller checks pending transactions every five seconds, so no public
callback URL is required for the Stokvel app.

## 7. Troubleshoot safely

| Response | Meaning | Action |
| --- | --- | --- |
| `401` | Token invalid or expired | Generate a new product token; verify API user/key. |
| `400` | Payload invalid | Check ZAR, MSISDN format, and required fields. |
| `404` | Reference not found | Use the exact UUID sent in `X-Reference-Id`. |
| `409` | Duplicate reference | Generate a new UUID; never reuse one. |
| `500` / `503` | MoMo service unavailable | Retry later with the same transaction only after checking its status. |

If credentials were placed in chat, source control, or a shared Postman export,
ask MTN to rotate them before continuing.
