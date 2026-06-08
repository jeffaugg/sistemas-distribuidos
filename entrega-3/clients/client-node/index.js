const axios = require('axios');

const BASE_URL = process.env.API_URL || 'http://localhost:8080/api/computadores';

async function listar() {
  const r = await axios.get(BASE_URL);
  console.log(r.status, r.data);
}

async function adicionar() {
  const payload = {
    codigo: 'MC-999',
    categoria: 'Microcomputador',
    marca: 'TestBrand',
    modelo: 'TM-1'
  };
  const r = await axios.post(BASE_URL, payload);
  console.log(r.status, r.data);
}

async function buscar(codigo) {
  const r = await axios.get(`${BASE_URL}/${codigo}`);
  console.log(r.status, r.data);
}

async function remover(codigo) {
  const r = await axios.delete(`${BASE_URL}/${codigo}`);
  console.log(r.status, r.data);
}

async function main() {
  await listar();
  await adicionar();
  await buscar('MC-999');
  await remover('MC-999');
}

main().catch(console.error);
