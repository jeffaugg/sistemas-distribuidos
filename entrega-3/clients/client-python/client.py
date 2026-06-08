import os

import requests

BASE_URL = os.environ.get("API_URL", "http://localhost:8080/api/computadores")


def listar():
    r = requests.get(BASE_URL)
    print(r.status_code, r.json())


def adicionar():
    payload = {
        "codigo": "NB-999",
        "categoria": "Notebook",
        "marca": "TestBrand",
        "modelo": "TB-1"
    }
    r = requests.post(BASE_URL, json=payload)
    print(r.status_code, r.text)


def buscar(codigo):
    r = requests.get(f"{BASE_URL}/{codigo}")
    print(r.status_code, r.json())


def remover(codigo):
    r = requests.delete(f"{BASE_URL}/{codigo}")
    print(r.status_code, r.text)


if __name__ == '__main__':
    listar()
    adicionar()
    buscar("NB-999")
    remover("NB-999")
