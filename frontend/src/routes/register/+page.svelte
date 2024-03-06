<script lang="ts">
	import { Container, Row, Col, Input, Button, Icon, Alert } from '@sveltestrap/sveltestrap';
	import NavBar from '../../components/NavBar.svelte';
	import { isLoggedIn, register, login } from '../../stores/userStore';

	var loginState: boolean;
	isLoggedIn.subscribe((value) => (loginState = value));

	let usernameValue = '';
	let passwordValue = '';

	let showAlert = false;
    let alertType = "danger";
	let alertMessage = '';

	function submit() {
		if (usernameValue.trim() === '') {
			showAlert = true;
			alertMessage = 'Please specify a username';
			return;
		}

		if (passwordValue.trim() === '') {
			showAlert = true;
			alertMessage = 'Please specify a password';
			return;
		}

		register(usernameValue, passwordValue)
			.then((statusText) => {
                alertType = "success";
                alertMessage = "Account registered";
		        showAlert = true;

				login(usernameValue, passwordValue);
			})
			.catch((error) => {
                alertType = "danger";
		        alertMessage = error;
                showAlert = true;
			});
	}
</script>

<NavBar></NavBar>

<Container>
	<Row>
		<Col>
			<h1 id="title"><Icon name="person-fill" /> Register</h1>
		</Col>
	</Row>

	<Row class="mt-4">
		<Col>
			<Alert
				children={alertMessage}
				color={alertType}
				dismissible={false}
				fade={true}
				isOpen={showAlert}
			/>
		</Col>
	</Row>

	<Row class="mt-4">
		<Col sm="4">
			<Input bind:value={usernameValue} placeholder="Username" />
		</Col>
	</Row>

	<Row class="mt-3">
		<Col sm="4">
			<Input type="password" bind:value={passwordValue} placeholder="Password" />
		</Col>
	</Row>

	<Row class="mt-3">
		<Col sm="4">
			<p>Already an account? <a href="/login">Login here</a></p>
		</Col>
	</Row>

	<Row>
		<Col>
			<Button color="primary" on:click={submit}>Register</Button>
		</Col>
	</Row>
</Container>

<style>
	h1, p, a {
		color: white;
	}

	p {
		font-size: 18px;
	}

	a {
		font-weight: bold;
		text-decoration: none;
	}

	#title {
		margin-top: 40px;
	}
</style>
