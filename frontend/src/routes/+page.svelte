<script>
	import { Container, Row, Col, Input, Button, Icon, Alert } from '@sveltestrap/sveltestrap';
	import NavBar from '../components/NavBar.svelte';

	let urlInputValue = '';
	let isUrlEmpty = false;

	function submit() {
		if (urlInputValue.trim() === '') {
			isUrlEmpty = true;
		} else {
			isUrlEmpty = false;
			// TODO: send url
		}
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
				children="Please specify a url"
				color="danger"
				dismissible={false}
				fade={true}
				isOpen={isUrlEmpty}
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
