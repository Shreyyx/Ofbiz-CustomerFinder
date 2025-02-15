<form name='findCustomer' method="post" action="<@ofbizUrl>findCustomer</@ofbizUrl>">
<div id="findCustomer" class="screenlet">
<div class="screenlet-body">
<table class="basic-table" cellspacing='0'>
<tr>
<td class='label'>${uiLabelMap.emailAddress}</td>
<td><input type='text' name='emailAddress'/></td>
</tr>
<tr>
<td class='label'>${uiLabelMap.firstName}</td>
<td ><input type='text' name='firstName'/></td>
</tr>
<tr>
<td class='label'>${uiLabelMap.lastName}</td>
<td ><input type='text' name='lastName'/></td>
</tr>
<tr>
<td class='label'>${uiLabelMap.contactNumber}</td>
<td ><input type='text' name='contactNumber'/></td>
</tr>
<tr>
<td class='label'>${uiLabelMap.address1}</td>
<td ><input type='text' name='address1'/></td>
</tr>
<tr>
<td class="label"/>
<td>
<input type="hidden" name="showAll" value="Y"/>
<input type='submit' value='${uiLabelMap.CommonFind}'/>
</td>
</tr>
</table>
</td>
</tr>
</table>
</div>
</div>
</form>