import { writable } from "svelte/store";
import { goto } from '$app/navigation';

export var isLoggedIn = writable(false);
export var token = "";

const BASE_URL = "http://localhost:8020"

export function register(username: string, password: string): Promise<string> {
    return new Promise((resolve, reject) => {
        fetch(new Request(BASE_URL + "/api/auth/register", {
            method: 'POST',
            body: JSON.stringify({
                "username": username,
                "password": password
            })
        })).then(response => {
            if (response.ok) {
                resolve("Register successfull");
            } else {
                reject(new Error(response.statusText));
            }
        }).catch(error => {
            console.error(error);
            reject(error.message);
        });
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
            setCookie('token', token, 30);
            setTimeout(() => goto('/'), 2000);
            resolve("Login successful");
        }).catch(error => {
            console.error("Error:", error);
            reject(error.message);
        });
    });
}

export function logout() {
    isLoggedIn.set(false);
    token = "";
    setCookie("token", "", -1);
    goto('/');
}

export function loadToken() {
    token = getCookie('token');
    if (token === '') {
        isLoggedIn.set(false);
        return;
    }

    isLoggedIn.set(true);
}

function setCookie(name: String, value: String, days: number) {
    var expires = "";
    if (days) {
        var date = new Date();
        date.setTime(date.getTime() + (days * 24 * 60 * 60 * 1000));
        expires = "; expires=" + date.toUTCString();
    }
    document.cookie = name + "=" + (value || "") + expires + "; SameSite=Lax; path=/;";
}

function getCookie(name: String) {
    var nameEQ = name + "=";
    var ca = document.cookie.split(';');
    for (var i = 0; i < ca.length; i++) {
        var c = ca[i];
        while (c.charAt(0) == ' ') c = c.substring(1, c.length);
        if (c.indexOf(nameEQ) == 0) return c.substring(nameEQ.length, c.length);
    }
    return "";
}

function getToken() {
    return token;
}

export default {
    login,
    register,
    loadToken,
    getCookie,
    setCookie,
    getToken
}