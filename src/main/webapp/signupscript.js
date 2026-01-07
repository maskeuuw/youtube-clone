// MINIMAL JS: Only for Toggling Password Visibility
        document.addEventListener('DOMContentLoaded', function() {
            function setupPasswordToggle(inputId, toggleBtnId) {
                const inputElement = document.getElementById(inputId);
                const toggleBtn = document.getElementById(toggleBtnId);

                if (inputElement && toggleBtn) {
                    toggleBtn.addEventListener('click', function() {
                        const type = inputElement.getAttribute('type') === 'password' ? 'text' : 'password';
                        inputElement.setAttribute('type', type);
                        this.querySelector('i').className = type === 'password' 
                            ? 'far fa-eye' 
                            : 'far fa-eye-slash';
                    });
                }
            }

            setupPasswordToggle('password', 'togglePassword');
            setupPasswordToggle('confirmPassword', 'toggleConfirmPassword');
        });