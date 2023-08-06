$(document).ready(function () {

});

async function iniciarSesion() {
    // Obtener los valores del nombre de usuario y la contraseña desde los campos de entrada en HTML
    const username = document.getElementById('txtUsuario').value;
    const password = document.getElementById('txtPasswordIniciar').value;

    const response = await fetch("/login", {
        method: 'POST',
        headers: {
            'Content-Type': 'application/json'
        },
        body: JSON.stringify({
            username: username,
            password: password
        })
    });

    if (!(response.ok)) {
        swal.fire({
            title: "Usuario o contraseña incorrectos",
            text: "Intente de nuevo",
            icon: "question",
            showConfirmButton: false,
            timer: 1500
        })
    } else {
        localStorage.token = response;
        localStorage.username = username ;
        swal.fire({
            title: "Bienvenido",
            icon: "success",
            showConfirmButton: false,
            timer: 1500
        })
        location.href = "index.html";
    }
}