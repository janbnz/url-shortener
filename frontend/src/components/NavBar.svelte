<script lang="ts">
	import { Navbar, NavbarBrand, NavItem, NavLink, Nav } from '@sveltestrap/sveltestrap';
	import { isLoggedIn, logout } from '../stores/userStore';

	var loginState: boolean;
	isLoggedIn.subscribe((value) => loginState = value);
</script>

<Navbar color="dark" dark expand="md" container="md">
	<NavbarBrand href="/">url-shortener</NavbarBrand>

	<Nav class="ms-auto" navbar>
		<NavItem>
			{#if loginState}
				<NavLink href="/" on:click={logout}>Logout</NavLink>
			{:else}
				<NavLink href="/login">Login</NavLink>
			{/if}
		</NavItem>
	</Nav>
</Navbar>

<style>
</style>
