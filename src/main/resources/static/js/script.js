document.addEventListener("DOMContentLoaded", () => {
    const updatePrice = (quantityInput) => {
        const row = quantityInput.closest("tr");
        const priceElement = row.querySelector(".product-price");
        const totalElement = row.querySelector(".product-total");

        const price = parseFloat(priceElement.dataset.price);
        let quantity = parseInt(quantityInput.value);

        // Validate quantity range
        if (quantity > 5) {
            quantity = 5;
            quantityInput.value = 5;
        } else if (quantity < 1 || isNaN(quantity)) {
            quantity = 1;
            quantityInput.value = 1;
        }

        // Update total price for the product
        totalElement.textContent = `Rs. ${(price * quantity).toFixed(2)}`;

        // Update grand total
        updateGrandTotal();
    };

    const updateGrandTotal = () => {
        let grandTotal = 0;
        document.querySelectorAll(".product-total").forEach((totalElement) => {
            const total = parseFloat(totalElement.textContent.replace("Rs. ", ""));
            grandTotal += total;
        });

        // Ensure you target the correct span for grand total
        const grandTotalElement = document.querySelector(".cart-summary .grand-total");
        if (grandTotalElement) {
            grandTotalElement.textContent = `Rs. ${grandTotal.toFixed(2)}`;
        }
    };

    // Attach event listeners to quantity inputs
    document.querySelectorAll(".quantity-input").forEach((input) => {
        input.addEventListener("input", () => updatePrice(input));
    });

    // Attach event listeners to update buttons
    document.querySelectorAll(".update-btn").forEach((btn) => {
        btn.addEventListener("click", (event) => {
            event.preventDefault(); // Prevent default form submission

            const form = event.target.closest("form");
            const quantity = form.querySelector(".quantity-input").value;

            // Submit the form via AJAX or normal submission
            form.submit(); // Uncomment this line for normal form submission
        });
    });

    // Initial grand total update
    updateGrandTotal();
});
