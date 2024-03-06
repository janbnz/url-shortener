<script lang="ts">
	import { Container, Row, Col, Input, Button, Icon, Alert } from '@sveltestrap/sveltestrap';
	import NavBar from '../components/NavBar.svelte';
	import { isLoggedIn } from '../stores/userStore';

	var loginState: boolean;
	isLoggedIn.subscribe((value) => (loginState = value));

	let urlInputValue = '';
	let showError = false;
	let errorMessage = '';

	function submit() {
		if (!loginState) {
			showError = true;
			errorMessage = 'You need an account before you can create short urls';
			return;
		}

		if (urlInputValue.trim() === '') {
			showError = true;
			errorMessage = 'Please specify a url';
			return;
		}

		showError = false;
		// TODO: send url
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
			<Alert
				children={errorMessage}
				color="danger"
				dismissible={false}
				fade={true}
				isOpen={showError}
			/>
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
