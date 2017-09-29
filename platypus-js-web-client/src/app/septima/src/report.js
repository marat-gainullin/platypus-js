define(function () {
    function Report(reportLocation) {
        function download(aName) {
            var a = document.createElement('a');
            a.download = aName ? aName : '';
            a.style.display = 'none';
            a.style.visibility = 'hidden';
            a.href = reportLocation;
            document.body.appendChild(a);
            a.click();
            document.body.removeChild(a);
        }
        this.show = function () {
            download();
        };
        this.print = function () {
            download();
        };
        this.save = function (aName) {
            download(aName);
        };
    }
    return Report;
});