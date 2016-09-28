package com.is.system.dao;

import java.util.List;

/** 
 * @author zhuolin(zl@nbicc.com) 
 * @date 2015年6月4日
 * 类说明 
 */
public interface CloudDao<T> {
	/**
	 * 添加
	 */
	public void add(T t);
	/**
	 * 批量添加
	 */
	public void batchAdd(List<T> list);
	/**
	 * 修改
	 */
	public void update(T t);
	/**
	 * 按照对象删除
	 */
	public void delete(T t);
	
	public void merge(T t);
	/**
	 * 批量删除
	 */
	public void batchDelete(List<T> list);
	/**
	 * 按照id删除
	 */
	public boolean deleteById(java.io.Serializable id);
	/**
	 * 查询所有
	 */
	public List<T> findAll();
	/**
	 * 按照id查询
	 */
	public T findById(java.io.Serializable id);
	/**
	 * 以对象作为查询条件进行查询
	 */
	public T findForObject(final T entity);
	/**
	 *根据HQL进行查询所有满足条件的集合
	 */
	public List<T> findByHql(String hql,Object...objects);
	/**
	 * 根据HQL进行查询唯一一条符合条件的记录
	 */
	public T getByHql(String hql,Object...objects);
	
	public List<T> findByLimit(String hql,int firstResult,int maxResults,Object...objects);

	public void deleteBySql(String sql);
}
