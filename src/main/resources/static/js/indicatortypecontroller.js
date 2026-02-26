document.addEventListener('DOMContentLoaded', () => {
    const typeSelect = document.getElementById('type');
    const stringFields = document.getElementById('stringFields');
    const hashFields = document.getElementById('hashFields');

    const stringInput = stringFields.querySelector('input');
    const hashInput = hashFields.querySelector('input');

    function toggleFields() {
        const selected = typeSelect.value;

        if (selected === 'STRING') {
            stringFields.style.display = 'block';
            hashFields.style.display = 'none';

            stringInput.disabled = false;
            hashInput.disabled = true;
        }
        else if (selected === 'IMAGE_HASH' || selected === 'VIDEO_HASH') {
            stringFields.style.display = 'none';
            hashFields.style.display = 'block';

            stringInput.disabled = true;
            hashInput.disabled = false;
        }
    }

    typeSelect.addEventListener('change', toggleFields);
    toggleFields();
});