window.on("load", function() {
    var contextButton = $("button.context");
    if (contextButton) {
        contextButton.on("click", function() {
            var popup = $(".contextPopup");
            if (popup.style.display !== "block") {
                popup.style.display = "block";
            } else {
                popup.style.display = "none";
            }
        });
    }

    var editButton = $("button.edit");
    if (editButton) {
        editButton.on("click", function() {
            $(".page-content").style.display = "none";
            $(".page-editor").style.display = "block";
            $("button.edit").disabled = true;
            $("button.edit").style.display = "none";
        });
    }
})