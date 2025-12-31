import http from 'k6/http';
import { check, sleep } from 'k6';

// Configuration: 50 users, 30 seconds
export const options = {
    vus: 50,
    duration: '30s',
};

export function setup() {
    const url = 'http://host.docker.internal:8080/api/accounts';
    const params = { headers: { 'Content-Type': 'application/json' } };

    const accountIds = [];

    for (let i = 0; i < 50; i++) {
        const payload = JSON.stringify({
            name: `User_${i}`,
            usdBalance: 10000000
        });

        const res = http.post(url, payload, params);

        if (res.status === 200 || res.status === 201) {
            accountIds.push(res.json('id'));
        }
    }

    console.log(`Setup created ${accountIds.length} accounts.`);
    return accountIds; // This array is passed to the default function
}

export default function (accountIds) {
    const url = 'http://host.docker.internal:8080/api/orders';

    const randomAccountId = accountIds[Math.floor(Math.random() * accountIds.length)];

    const payload = JSON.stringify({
        accountId: randomAccountId,
        priceLimit: 50000,
        amount: 0.01
    });

    const params = { headers: { 'Content-Type': 'application/json' } };

    const res = http.post(url, payload, params);

    check(res, {
        'is status 200': (r) => r.status === 200,
    });

    sleep(0.01);
}