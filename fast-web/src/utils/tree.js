/**
 * 将扁平数组转换为树形结构
 *
 * @param data 扁平数组
 * @param id ID字段名
 * @param parentId 父ID字段名
 * @param children 子节点字段名
 * @returns 树形结构数组
 */
export function handleTree(data, id = 'id', parentId = 'parentId', children = 'children') {
  const config = {
    id,
    parentId,
    childrenList: children
  }

  const childrenListMap = {}
  const nodeIds = {}
  const tree = []

  for (const d of data) {
    const pId = d[config.parentId]
    if (!childrenListMap[pId]) {
      childrenListMap[pId] = []
    }
    nodeIds[d[config.id]] = d
    childrenListMap[pId].push(d)
  }

  for (const d of data) {
    const pId = d[config.parentId]
    if (!nodeIds[pId]) {
      tree.push(d)
    }
  }

  for (const t of tree) {
    adaptToChildren(t)
  }

  function adaptToChildren(o) {
    if (childrenListMap[o[config.id]]) {
      o[config.childrenList] = childrenListMap[o[config.id]]
    }
    if (o[config.childrenList]) {
      for (const c of o[config.childrenList]) {
        adaptToChildren(c)
      }
    }
  }

  return tree
}