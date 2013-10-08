/* 
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */


$(document).ready(function() {
    $('#beanForm').on('submit', function() {

        $.ajax({
            url: $(this).attr('action'), // le nom du fichier indiqué dans le formulaire
            type: $(this).attr('method'), // la méthode indiquée dans le formulaire (get ou post)
            data: $(this).serialize(), // je sérialise les données (voir plus loin), ici les $_POST
            dataType: 'json',
            success: function(html) { // je récupère la réponse du fichier PHP
                // Suppression des erreurs précédentes
                var spanEl = $('#beanForm span.erreur');
                var i;
                for (i = 0; i < spanEl.length; i++) {
                    $(spanEl[i]).empty();
                }

                var valid = html['valid'];
                if (valid) {
                    var opOk = html['OperationOk'];
                    if (opOk) {
                        var redirUrl = html['redirUrl'];
//                    alert("Action réussie. Vous allez être redirigé vers  : " + redirUrl);

                        $('nav').append('<div id="dia" title="tttitre"><p><strong>Oppération effectué</strong></p><p>Vous allez être redirigé dans 3 secondes à l\'adresse <a href="' + redirUrl + '">' + redirUrl + '</a></p></div>');
                        $('#dia').dialog({minHeight: 300, minWidth: 400, closeText: "hide", show: "fade", dialogClass: "alert"});
//                    $('#page').empty();
//            setTimeout("location.href = 'http://zouzou}';",3000);

                        setTimeout("location.href = '" + redirUrl + "';", 3000);
                    }
                    else {
                        alert('erreur : ' + html['resultat']);
                    }
                }
                else {

                    alert('Erreur lors de la saisie');
                    var errTab = html['erreurs'];
                    i = 0;
                    for (i = 0; i < errTab.length; i++) {
                        var err = errTab[i];
                        var errEl = 'err' + err['key'];
                        var errContenu = err['value'];
                        $('#' + errEl).append(errContenu);
                    }
                }
            },
            error: function() {
                alert('erreur serveur lors de la soumission');

            }

        });
        return false; // Il faut empécher le submit
    });



});