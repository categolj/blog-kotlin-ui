var request = window.superagent;
var hljs = window.hljs;

function loadContent(doc, entryId) {
    doc.innerText = 'Loading...';
    doc.disabled = true;
    doc.className = 'button is-info is-loading is-small';
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
            a.className = 'button is-small';
            a.text = 'Share';
            parent.appendChild(a);
        });
}

function subscribe(doc, entryId) {
    if (confirm('Are you sure to subscribe?')) {
        doc.innerText = 'Sending...';
        doc.disabled = true;
        doc.className = 'button is-info is-loading is-small';
        var token = document.cookie.match(/XSRF-TOKEN=([a-zA-Z0-9\-]+)/)[1];
        request.post('/premium/subscribe')
            .send({entryId: entryId})
            .set('X-XSRF-TOKEN', token)
            .end(function (err, res) {
                var parent = doc.parentElement.parentElement;
                if (res.ok) {
                    parent.innerHTML = '✅ Subscribed. Thanks!<br> It might take a few minutes to be reflected. If there is any trouble, please reach out <a href="https://twitter.com/making">@making</a><br><br>';
                    var a = document.createElement('a');
                    a.setAttribute('href', '/p/entries/' + entryId);
                    a.className = 'button is-info is-small';
                    a.text = 'Reload';
                    a.onclick = function () {
                        a.innerText = 'Loading...';
                        a.disabled = true;
                        a.className = 'button is-info is-loading is-small';
                    };
                    parent.appendChild(a);
                } else {
                    parent.parentElement.className = 'message is-danger';
                    var body = res.body;
                    parent.innerHTML = '🚨 ' + body.message + '<br>' + (body.detail && (body.detail + '<br>'));
                }
            });
    }
}