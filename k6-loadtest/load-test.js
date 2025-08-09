import http from 'k6/http';
import { check, sleep, group } from 'k6';
import { Trend } from 'k6/metrics';

export let options = {
  vus: 50,
  duration: '20s',
  thresholds: {
    // 95% das requisições COM cache < 300ms
    'cache_req_duration': ['p(95)<300'],
    // 95% das requisições SEM cache < 800ms
    'nocache_req_duration': ['p(95)<800'],
  },
};

// Trends para medir tempos com/sem cache
const cacheTrend    = new Trend('cache_req_duration');
const nocacheTrend  = new Trend('nocache_req_duration');

// 1) Generating test data once before load begins
export function setup() {
  const amount = 100;
  const res = http.post(
    `http://localhost:8080/orders/generate-test-data?amount=${amount}`,
    null,
    { headers: { 'X-Request-Id': 'setup' } }
  );
  if (res.status !== 200) {
    throw new Error(`Erro ao gerar pedidos de teste: ${res.status} - ${res.body}`);
  }
  return JSON.parse(res.body); // retorna array de UUIDs
}

// 2) Load test: para cada VU, pega um ID aleatório e faz COM CACHE / SEM CACHE
export default function (ids) {
  const id = ids[Math.floor(Math.random() * ids.length)];
  const headers = { 'X-Request-Id': `vu${__VU}-it${__ITER}` };

  group('COM CACHE', () => {
    const res = http.get(`http://localhost:8080/orders/${id}`, { headers });
    cacheTrend.add(res.timings.duration);
    check(res, { 'cache 200': (r) => r.status === 200 });
  });

  group('SEM CACHE', () => {
    const res = http.get(`http://localhost:8080/orders/no-cache/${id}`, { headers });
    nocacheTrend.add(res.timings.duration);
    check(res, { 'nocache 200': (r) => r.status === 200 });
  });

  sleep(0.3);
}