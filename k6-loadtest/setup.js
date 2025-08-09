import http from 'k6/http';

export function setup() {
    const amount = 100; // número de pedidos
    const headers = { 'X-Request-Id': 'test-setup' };
    const res = http.post(`http://localhost:8080/orders/generate-test-data?amount=${amount}`, null, { headers });

    if (res.status !== 200) {
        throw new Error(`Erro ao gerar pedidos de teste: ${res.status}`);
    }

    return JSON.parse(res.body); // retorna array de UUIDs para usar no teste
}