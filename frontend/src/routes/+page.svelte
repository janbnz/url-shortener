<script lang="ts">
	import { Container, Row, Col, Input, Button, Icon, Alert } from '@sveltestrap/sveltestrap';
	import NavBar from '../components/NavBar.svelte';
	import { isLoggedIn } from '../stores/userStore';
	import { createUrl } from '../stores/urlStore';

	var loginState: boolean;
	isLoggedIn.subscribe((value) => (loginState = value));

	let urlInputValue = '';
	let newURL = '';

	let showAlert = false;
	let alertType = 'danger';
	let alertMessage = '';

	function submit() {
		if (!loginState) {
			showAlert = true;
			alertMessage = 'You need an account before you can create short urls';
			alertType = 'danger';
			return;
		}

		if (urlInputValue.trim() === '') {
			showAlert = true;
			alertMessage = 'Please specify a url';
			alertType = 'danger';
			return;
		}

		createUrl(urlInputValue)
			.then((statusText) => {
				alertType = 'success';
				alertMessage = 'URL created';
				newURL = statusText;
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
			<h1 id="title"><Icon name="house-fill" /> Home</h1>
		</Col>
	</Row>

	<Row class="mt-4">
		<Col>
			<Alert color={alertType} dismissible={false} fade={true} isOpen={showAlert}>
				{#if alertType == 'success'}
					<span>{alertMessage} <a target="_blank" href={newURL}>{newURL}</a></span>
				{:else}
					<span>{alertMessage}</span>
				{/if}
			</Alert>
		</Col>
	</Row>

	<Row class="mt-4">
		<Col sm="4">
			<Input bind:value={urlInputValue} placeholder="URL" />
		</Col>

		<Col sm="4">
			<Button color="primary" on:click={submit}>Create URL</Button>
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
</style>
