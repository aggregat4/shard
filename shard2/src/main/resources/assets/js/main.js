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
        pagePreview.innerHTML = "<div class=\"page-preview-top\"></div>" + marked(pageEditor.value);
    };
    var updatePreview = aslovok.debounce(immediateUpdatePreview, 500, false);

    // TODO figure out how to trigger editing with accesskey as well
    var editButton = $("button.edit");
    if (editButton) {
        editButton.on("click", function() {
            $(".page-content").style.display = "none";
            $(".page-content-editing").style.display = "block";
            $(".page-editor").style.display = "block";
            $(".page-preview").style.display = "block";
            editButton.disabled = true;
            editButton.style.display = "none";
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
        pageEditor.on("scroll", aslovok.debounce(function () {
            var editorScrollRange = pageEditor.scrollHeight - pageEditor.clientHeight;
            var previewScrollRange = pagePreview.scrollHeight - pagePreview.clientHeight;
            // Find how far along the editor is (0 means it is scrolled to the top, 1
            // means it is at the bottom).
            var scrollFactor = pageEditor.scrollTop / editorScrollRange;
            // Set the scroll position of the preview pane to match.  jQuery will
            // gracefully handle out-of-bounds values.

            var newScrollPosition = Math.min(scrollFactor * previewScrollRange, pagePreview.scrollHeight);
            //pagePreview.scrollTop = newScrollPosition;
            var pagePreviewTop = $(".page-preview-top");
            Velocity(pagePreviewTop, "scroll", {
                container: pagePreview,
                offset: newScrollPosition,
                duration: 1000,
                easing: "easeInOutCubic" });
        }, 200, false));
    }

})