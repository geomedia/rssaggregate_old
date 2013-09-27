/* 
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */


function changepass() {
    if ($('#btchgpass').val() === '0') {
        $('#chgpass').append('<label>Mot de pass</label><input name="pass1" type="password" id="pass1"/> <br /> <label>Retaper : </label><input name="pass2" type="password" id="pass2"/>');
        $('#btchgpass').text('Annuler le changement de mot de passe');
        $('#btchgpass').val('1')
    }
    else if ($('#btchgpass').val() === '1') {
        $('#btchgpass').val(0);
        $('#chgpass').empty();
        $('#btchgpass').text('Changer de mot de passe');
    }
}
function  subfunction() {
    if ($('#btchgpass').val() === '0') {
        $('#form').submit();
    }
    else if ($('#btchgpass').val() === '1') {
        if ($('#pass1').val() === $('#pass2').val()) {
            //Exeptionnellement on fait une vérification de mot saisie coté client. Elle est a réeffectuer coté serveur mais du fait de l'enploi de l'ajout dynamique de la div contenant des mot de passe, on ne peut notifier le client par la voie classique.
            if ($('#pass1').val().length < 3) {
                alert('mot de passe trop court');
            } else {
                $('#form').submit();
            }
        }
        else {
            alert('Les deux mots de pass ne conindent pas !!');
        }
    }
}