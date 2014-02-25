/* 
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

/***
 * Fonction permettant le dragdrop dans la liste de journaux
 */
$(document).ready(function() {

//    $('#fluxSelection li').draggable({helper: "clone", connectWith: ".connectedSortable"})
    ;
    $("#fluxSelection, #fluxSelection2").sortable({
        connectWith: ".connectedSortable",
        remove: uiRemove,
        placeholder: "sortable-placeholder",
        helper: "clone",
        revert: true
    });



    /***
     * Ajout de toutes les items de la gauche vers la droite
     */
    $('#btAddAll').on('click', function() {
        elementLi = $('#fluxSelection li');
        i = 0;
        for (i = 0; i < elementLi.length; i++) {
            el = elementLi[i];
//           $('#fluxSelection2').add($(el));
//           $('#fluxSelection2').append("<li></li>")
            elId = $(el).val();
            elTxt = $(el).text();

            // On cherche si l'item n'est pas déjà présente dans la colonne de droite
            elementLidroite = $('#fluxSelection2 li');
            trouve = false;
            j = 0;
            for (j = 0; j < elementLidroite.length; j++) {
                elJ = elementLidroite[j];
                if ($(elJ).val() === elId) {
                    trouve = true;
                }
            }

            if (!trouve) {
                $('#fluxSelection2').append('<li class=\"boxelement\" value="' + elId + '">' + elTxt + '</li>');
                elementLi.remove(i)
            }
        }
    });



    /***
     * Passage de toutes les items de la droite vers la gauche
     */
    $('#btRemAll').on('click', function() {
        elementLidroite = $('#fluxSelection2 li');
        i = 0;
        for (i = 0; i < elementLidroite.length; i++) {
            elDroite = elementLidroite[i];
            elId = $(elDroite).val();
            elTxt = $(elDroite).text();

            // On test si l'item est déjà dans la colonne de gauche
            trouve = false;
            j = 0;
            elementLigauche = $('#fluxSelection li');

            for (j = 0; j < elementLigauche.length; j++) {
                elJ = elementLigauche[j];
                if ($(elJ).val() === elId) {
                    trouve = true;
                }
            }
            if (!trouve) {
//                $(elementLigauche).append("<li>Tata</li>");
                $('#fluxSelection').append('<li class=\"boxelement\" value="' + elId + '">' + elTxt + '</li>');
            }
            elementLidroite.remove(i)

        }
    });


//    $("#fluxSelection li").draggable({helper: "clone"});
});


/***
 * Lorsqu'un élément est recut dans la liste 2 il faut vérifier qu'il n'est pas déjà présent. Cette fontion permet d'annuler le drop si l'élémetn est déjà présent
 * @param {typeStr} event
 * @param {typeStr} ui
 * @returns {Boolean}
 */
function uiRemove(event, ui) {
    trouve = 0;
    for (i = 0; i < $('#fluxSelection2 li').length; i++) {
        if (ui['item'].val() === $('#fluxSelection2 li')[i]['value']) {
            trouve++;
        }
    }
    if (trouve > 1) {
        return false;
    }
}

$(document).ready(function() {

    // à la sélection d une région dans la liste
    $('#journalSelection').on('change', function truc() {
        var val = $(this).val(); // on récupère la valeur de la région

        if (val != '') {
            $('#fluxSelection').empty(); // on vide la liste des départements
            $.ajax({
                url: '/RSSAgregate/flux/list?vue=json',
                data: 'journalid=' + val, // on envoie $_GET['id_region']
                dataType: 'json',
                success: function(json) {

                    // On trie coté client les flux en fonction de leur type. Le but est de faire apparaitre international et a la une en tete



                    $.each(json, function(index, value) {



                        $('#fluxSelection').append('<li class=\"boxelement\" value="' + value[0] + '">' + value[1] + '</li>');
                    });
                }
            });
        }
    });
    $('#typeSelection').on('change', function truc() {
//          alert('chg');
        // On récupère le type
        type = $('#typeSelection').val();
//          alert('type : ' + type);

        journal = $('#journalSelection').val();
        ajaxLunch(type, journal);



    });

    function isID(input) {
        var RE = /^\d*$/;
        return (RE.test(input));
    }


    function ajaxLunch(typeStr, journalStr) {

        datareq = '&';

        if (isID(typeStr)) {
            datareq += 'typeid=' + typeStr + "&";
        }

        if (isID(journalStr)) {
            datareq += 'journalid=' + journalStr;
        }


        $('#fluxSelection').empty(); // on vide la liste des départements
        $.ajax({
            url: '/RSSAgregate/flux/list?vue=json',
            data: datareq, // on envoie $_GET['id_region']
            dataType: 'json',
            success: function(json) {

                // On trie coté client les flux en fonction de leur type. Le but est de faire apparaitre international et a la une en tete



                $.each(json, function(index, value) {



                    $('#fluxSelection').append('<li class=\"boxelement\" value="' + value[0] + '">' + value[1] + '</li>');
                });
            }
        });


    }


}
);

// Ajoute un flux de la liste de gauche à la liste de droite en vérifiant que ce flux n'est pas déja ajouté. 
function selectflux() {
    $('#fluxSelection  option:selected').each(function(aaa) {
        i = 0;
        var test = $('#fluxSelection2  option');
        ajout = true;
        for (j = 0; j < test.length; j++) {
            if (test[j].value === $(this).val()) {
                ajout = false;
            }
        }
        if (ajout) {
            $('#fluxSelection2').append('<option value="' + $(this).val() + '">' + $(this).text() + '</option>');
        }
    }
    );
}

//// Supprimer le ou les flux sélectionné de la liste de droite
//function supp() {
//    // récup de la selection dans la liste 2
//    $('#fluxSelection2  option:selected').each(function(aaa) {
//        $(this).remove();
//    }
//
//    );
//
//}

