import { writable } from "svelte/store";
import { goto } from '$app/navigation';

export var isLoggedIn = writable(false);
export var token = "";

const BASE_URL = "http://localhost:8020"

export function register(username: string, password: string) {
    fetch(new Request(BASE_URL + "/api/auth/register", {
        method: 'POST',
        body: JSON.stringify({
            "username": username,
            "password": password
        })
    })).then(response => {
        if (response.status == 200) {
            login(username, password);
        } else {
            console.log("Error!");
        }
    });
}

export function login(username: string, password: string): Promise<string> {
    return new Promise((resolve, reject) => {
        fetch(new Request(BASE_URL + "/api/auth/login", {
            method: 'POST',
            body: JSON.stringify({
                "username": username,
                "password": password
            })
        })).then(response => {
            if (response.ok) {
                return response.json();
            } else {
                throw new Error(response.statusText);
            }
        }).then(data => {
            token = data.token;
            isLoggedIn.set(true);
            setTimeout(() => goto('/'), 2000);
            resolve("Login erfolgreich!");
        }).catch(error => {
            console.error("Error:", error);
            reject(error.message);
        });
    });
}

export default {
    login,
    register
}