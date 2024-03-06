<svelte:head>
	<title>url-shortener</title>
</svelte:head>

<slot />

<script lang="ts">
	import { loadToken } from '../stores/userStore';
	import { onMount } from 'svelte';

	onMount(() => {
		loadToken();
	});
</script>

<style>
	:global(body) {
		background-color: #1e2024;
		margin: 0;
		padding: 0;
	}
</style>