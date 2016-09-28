package com.is.dao.impl;

import java.io.Serializable;
import java.util.List;

import org.hibernate.Query;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import com.is.system.dao.CloudDao;
import com.is.util.CommonUtil;




/**
 * @author zhuolin(zl@nbicc.com)
 * @date 2015年6月4日 类说明
 */
@Repository("cloudDao")
public class CloudDaoImpl<T> implements CloudDao<T> {

	
	@Autowired
	private SessionFactory sessionFactory;
	
	private static final int batchSize = 10;

	@Override
	public void add(T t){
		getSession().persist(t);
	}

	@Override
	public void update(T t){
		getSession().update(t);
	}

	@Override
	public void delete(T t){
		getSession().delete(t);
	}
	
	@Override
	public void merge(T t) {
		getSession().merge(t);
	};

	@Override
	public boolean deleteById(Serializable id){
		T t = findById(id);
		if (t == null){
			return false;
		}
		getSession().delete(t);
		return true;
	}

	@Deprecated
	@Override
	public List<T> findAll(){
		// TODO Auto-generated method stub
		return null;
	}

	@Deprecated
	@Override
	public T findById(Serializable id) {
		return null;
//		return (T) (getSession().get(persistentClass, id));
	}

	@Deprecated
	@Override
	public T findForObject(T entity) {
		// TODO Auto-generated method stub
		return null;
	}

	public Session getSession() {
		return sessionFactory.getCurrentSession();
	}

	@Override
	public List<T> findByHql(String hql, Object... objects) {
		Query query = getSession().createQuery(hql);
		for (int i = 0; i < objects.length; i++) {
			query.setParameter(i, objects[i]);
		}
		return query.list();
	}

	@Override
	public T getByHql(String hql, Object... objects) {
		Query query = getSession().createQuery(hql);
		for (int i = 0; i < objects.length; i++) {
			query.setParameter(i, objects[i]);
		}
		List<T> list = query.list();
		T t = null;
		if(CommonUtil.isEmpty(list) || list.size() > 1){
			t = null;
		}else{
			t = list.get(0);
		}
		return t;
	}

	@Override
	public void batchAdd(List<T> list) {
		Session session = getSession();
		if (!CommonUtil.isEmpty(list)) {
			int listSize = list.size();
			for (int i = 0; i < listSize; i++) {
				session.persist(list.get(i));
				if ((i + 1) % batchSize == 0) {
					session.flush();
					session.clear();
				}
			}
		}
	}

	@Override
	public List<T> findByLimit(String hql, int firstResult, int maxResults,
			Object... objects) {
		Query query = getSession().createQuery(hql);
		query.setFirstResult(firstResult);
		query.setMaxResults(maxResults);
		for (int i = 0; i < objects.length; i++) {
			query.setParameter(i, objects[i]);
		}
		return query.list();
	}

	@Override
	public void deleteBySql(String sql) {
		Query query = getSession().createSQLQuery(sql);
		query.executeUpdate();		
	}

	@Override
	public void batchDelete(List<T> list) {
		Session session = getSession();
		if (!CommonUtil.isEmpty(list)) {
			int listSize = list.size();
			for (int i = 0; i < listSize; i++) {
				session.delete(list.get(i));
				if ((i + 1) % batchSize == 0) {
					session.flush();
					session.clear();
				}
			}
		}
	}
}
