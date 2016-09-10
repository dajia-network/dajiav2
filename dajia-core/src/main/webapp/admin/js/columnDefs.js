var ColumnDefs = ColumnDefs || {};

ColumnDefs = {
	productGridDef : [
			{
				name : '操作',
				width : 130,
				cellTemplate : '<div class="ui-grid-cell-contents"><button type="button" class="btn btn-primary btn-sm" ng-disabled="row.entity.productStatus!=2" ng-click="grid.appScope.bot(row.entity.product.productId);">BOT+1</button><button type="button" class="btn btn-primary btn-sm" ng-confirm-click="确定要删除该产品吗?" confirmed-click="grid.appScope.delProduct(row.entity.product.productId);">删除</button></div>'
			},
			{
				name : 'ID',
				field : 'product.productId',
				width : 50
			},
			{
				name : '产品名',
				field : 'product.name',
				width : 250,
				cellTemplate : '<div class="ui-grid-cell-contents"><a href ng-click="grid.appScope.editProduct(row.entity.product.productId);">{{row.entity.product.name}}</a></div>'
			},
			{
				name : '当前价格',
				field : 'currentPrice',
				width : 90
			},
			{
				name : '库存',
				field : 'stock',
				width : 60
			},
			{
				name : '销量',
				field : 'sold',
				width : 60
			},
			{
				name : '真实销量',
				field : 'realSold',
				width : 60
			},
			{
				name : '置顶',
				field : 'fixTop',
				width : 60
			},
			{
				name : '打群价',
				field : 'isPromoted',
				width : 60
			},
			{
				name : '产品状态',
				field : 'status4Show',
				width : 90
			},
			{
				name : '起始时间',
				field : 'startDate',
				width : 140,
				cellTemplate : "<div class='ui-grid-cell-contents'>{{row.entity.startDate | date:'yyyy-MM-dd HH:mm'}}</div>"
			},
			{
				name : '过期时间',
				field : 'expiredDate',
				width : 140,
				cellTemplate : "<div class='ui-grid-cell-contents'>{{row.entity.expiredDate | date:'yyyy-MM-dd HH:mm'}}</div>"
			} ]
}