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

    var immediateUpdatePreview = function (event) {
        pagePreview.innerHTML = marked(pageEditor.value);
    };
    var updatePreview = aslovok.debounce(immediateUpdatePreview, 500, false);

    // TODO figure out how to trigger editing with accesskey as well
    var editButton = $("a.edit");
    if (editButton) {
        editButton.on("click", function() {
            $(".page-content").style.display = "none";
            $(".page-editor").style.display = "block";
            $(".page-preview").style.display = "block";
            $("button.edit").disabled = true;
            $("button.edit").style.display = "none";
            immediateUpdatePreview();
        });
    }

    var pagePreview = $(".page-preview");
    var pageEditor= $(".page-editor-textarea");
    if (pagePreview && pageEditor) {
        pageEditor.on("keyup", updatePreview);
        pageEditor.on("change", updatePreview);
        pageEditor.on("paste", updatePreview);
        pageEditor.on("cut", updatePreview);
    }
})