document.addEventListener('DOMContentLoaded', function() {
            const form = document.getElementById('loginForm');
            const emailInput = document.getElementById('email');
            const passwordInput = document.getElementById('password');
            const togglePasswordBtn = document.getElementById('togglePassword');
            const loginBtn = document.getElementById('loginBtn');
            const emailError = document.getElementById('emailError');
            const passwordError = document.getElementById('passwordError');
            
            // Toggle password visibility
            togglePasswordBtn.addEventListener('click', function() {
                const type = passwordInput.getAttribute('type') === 'password' ? 'text' : 'password';
                passwordInput.setAttribute('type', type);
                this.innerHTML = type === 'password' 
                    ? '<i class="far fa-eye"></i>' 
                    : '<i class="far fa-eye-slash"></i>';
            });
            
            // Form validation
            form.addEventListener('submit', function(e) {
                e.preventDefault();
                
                let isValid = true;
                
                // Clear previous errors
                emailError.classList.remove('show');
                passwordError.classList.remove('show');
                emailInput.classList.remove('is-invalid');
                passwordInput.classList.remove('is-invalid');
                
                // Email validation
                const emailRegex = /^[^\s@]+@[^\s@]+\.[^\s@]+$/;
                if (!emailInput.value.trim() || !emailRegex.test(emailInput.value)) {
                    emailInput.classList.add('is-invalid');
                    emailError.classList.add('show');
                    isValid = false;
                }
                
                // Password validation
                if (!passwordInput.value.trim()) {
                    passwordInput.classList.add('is-invalid');
                    passwordError.classList.add('show');
                    isValid = false;
                }
                
                if (isValid) {
                    // Show loading state
                    const originalText = loginBtn.innerHTML;
                    loginBtn.innerHTML = '<span class="spinner-border spinner-border-sm me-2" role="status" aria-hidden="true"></span>Signing in...';
                    loginBtn.disabled = true;
                    
                    // Submit the form after a short delay to show loading state
                    setTimeout(() => {
                        form.submit();
                    }, 500);
                }
            });
            
            // Real-time validation
            emailInput.addEventListener('input', function() {
                if (this.value.trim()) {
                    this.classList.remove('is-invalid');
                    emailError.classList.remove('show');
                }
            });
            
            passwordInput.addEventListener('input', function() {
                if (this.value.trim()) {
                    this.classList.remove('is-invalid');
                    passwordError.classList.remove('show');
                }
            });
            
            
        });