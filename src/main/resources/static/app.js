var request = window.superagent;
var hljs = window.hljs;

function loadContent(doc, entryId) {
    doc.innerText = 'Loading...';
    doc.disabled = true;
    doc.className = 'button is-info is-loading';
    request
        .get('/entries/' + entryId + '?partial')
        .end(function (err, res) {
            var parent = doc.parentElement;
            parent.innerHTML = res.text;
            var elements = parent.querySelectorAll('pre > code');
            for (var i = 0; i < elements.length; i++) {
                hljs.highlightBlock(elements[i]);
            }
            var a = document.createElement('a');
            a.setAttribute('href', '/entries/' + entryId + '#share');
            a.className = 'button';
            a.text = 'Share';
            parent.appendChild(a);
        });
}