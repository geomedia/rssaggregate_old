
$(document).ready(function() {

    var $journalLie = $('#journalLie');
    var $parentFlux = $('#parentFlux');



    // à la sélection d une région dans la liste
    $journalLie.on('change', function truc() {
        var val = $(this).val(); // on récupère la valeur de la région

        if (val != '') {
            $parentFlux.empty(); // on vide la liste des départements

            $.ajax({
                url: 'flux?action=list&vue=json',
                data: 'journal-id=' + val, // on envoie $_GET['id_region']
                dataType: 'json',
                success: function(json) {
                    $parentFlux.append('<option value=NULL>NULL</option>');
                    $.each(json, function(index, value) {

                        $parentFlux.append('<option value="' + value[0] + '">' + value[1] + '</option>');
                    });
                }
            });
        }
    });
    

   
   
   
});