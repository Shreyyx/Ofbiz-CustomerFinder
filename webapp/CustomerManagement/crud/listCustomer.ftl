<table class="table table-bordered table-striped table-hover">
<thead>
<tr>
<th>${uiLabelMap.partyId}</th>
<th>${uiLabelMap.roleTypeId}</th>
<th>${uiLabelMap.firstName}</th>
<th>${uiLabelMap.lastName}</th>
<th>${uiLabelMap.address1}</th>
<th>${uiLabelMap.city}</th>
<th>${uiLabelMap.postalCode}</th>
<th>${uiLabelMap.areaCode}</th>
<th>${uiLabelMap.contactNumber}</th>
<th>${uiLabelMap.emailAddress}</th>
</tr>
</thead>

<#if customerList?has_content>
<tbody>
<#list customerList as result>
<tr>
<td>${result.get("partyId")!""}</td>
<td>${result.get("roleTypeId")!""}</td>
<td>${result.get("firstName")!""}</td>
<td>${result.get("lastName")!""}</td>
<td>${result.get("address1")!""}</td>
<td>${result.get("city")!""}</td>
<td>${result.get("postalCode")!""}</td>
<td>${result.get("areaCode")!""}</td>
<td>${result.get("contactNumber")!""}</td>
<td>${result.get("emailAddress")!""}</td>
</tr>
</#list>
</tbody>
<#else>
<tbody>
<tr><td colspan="12"> </td></tr>
</tbody>
</#if>

</table>