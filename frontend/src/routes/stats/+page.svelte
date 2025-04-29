<script lang="ts">
	import { Container, Row, Col, Input, Button, Icon, Alert } from '@sveltestrap/sveltestrap';
	import NavBar from '../../components/NavBar.svelte';
	import { isLoggedIn } from '../../stores/userStore';
	import { getStats } from '../../stores/urlStore';

	var loginState: boolean;
	isLoggedIn.subscribe((value) => (loginState = value));

	let urlInputValue = '';

	let showAlert = false;
	let alertType = 'danger';
	let alertMessage = '';

	function submit() {
		if (urlInputValue.trim() === '') {
			showAlert = true;
			alertMessage = 'Please specify a url';
			alertType = 'danger';
			return;
		}

		// Remove domain path if existing
		urlInputValue = urlInputValue.includes('/') ? urlInputValue.substring(urlInputValue.lastIndexOf('/') + 1) : urlInputValue;

		getStats(urlInputValue)
			.then((response) => {
				console.log(response);
				alertType = 'success';
				alertMessage = 'Shortened URL: ' + urlInputValue + "\nOriginal URL: " + response.originalUrl + "\nRedirects: " + response.redirectCount;
				showAlert = true;
			})
			.catch((error) => {
				alertType = 'danger';
				alertMessage = error;
				showAlert = true;
			});
	}
</script>

<NavBar></NavBar>

<Container>
	<Row>
		<Col>
			<h1 id="title"><Icon name="globe" /> Stats</h1>
		</Col>
	</Row>

	<Row class="mt-4">
		<Col>
			<Alert color={alertType} dismissible={false} fade={true} isOpen={showAlert}>
				<span>{alertMessage}</span>
			</Alert>
		</Col>
	</Row>

	<Row class="mt-4">
		<Col sm="4">
			<Input bind:value={urlInputValue} placeholder="URL" />
		</Col>

		<Col sm="4">
			<Button color="primary" on:click={submit}>Get stats</Button>
		</Col>
	</Row>
</Container>

<style>
	h1 {
		color: white;
	}

	#title {
		margin-top: 40px;
	}

	span {
		white-space: pre-line;
	}
</style>
