// Kept the essential subscribe button script
document.getElementById('subscribeBtn').addEventListener('click', function() {
	const btn = this;
	const isSubscribed = btn.classList.contains('subscribed');
	if (isSubscribed) {
		btn.classList.remove('subscribed');
		btn.innerHTML = '<i class="bi bi-bell-fill"></i> Subscribe';
	} else {
		btn.classList.add('subscribed');
		btn.innerHTML = '<i class="bi bi-bell-slash-fill"></i> Subscribed';
	}
});