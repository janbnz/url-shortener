<script>
	import { Navbar, NavbarBrand, NavItem, NavLink, Nav } from '@sveltestrap/sveltestrap';
</script>

<Navbar color="dark" dark expand="md" container="md">
	<NavbarBrand href="/">url-shortener</NavbarBrand>

	<Nav class="ms-auto" navbar>
		<NavItem>
			<NavLink href="/login">Login</NavLink>
		</NavItem>
	</Nav>
</Navbar>

<style>
    
</style>