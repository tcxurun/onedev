package com.pmease.gitop.web.editable.user;

import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.List;

import org.apache.wicket.Component;
import org.apache.wicket.markup.html.basic.Label;
import org.apache.wicket.model.IModel;
import org.eclipse.jgit.util.StringUtils;

import com.pmease.commons.editable.EditableUtils;
import com.pmease.commons.editable.PropertyDescriptor;
import com.pmease.commons.editable.PropertyDescriptorImpl;
import com.pmease.commons.hibernate.dao.Dao;
import com.pmease.commons.wicket.editable.BeanContext;
import com.pmease.commons.wicket.editable.EditSupport;
import com.pmease.commons.wicket.editable.NotDefinedLabel;
import com.pmease.commons.wicket.editable.PropertyContext;
import com.pmease.commons.wicket.editable.PropertyEditor;
import com.pmease.gitop.core.Gitop;
import com.pmease.gitop.core.editable.UserChoice;
import com.pmease.gitop.model.User;

@SuppressWarnings("serial")
public class UserEditSupport implements EditSupport {

	@Override
	public BeanContext<?> getBeanEditContext(Class<?> beanClass) {
		return null;
	}

	@Override
	public PropertyContext<?> getPropertyEditContext(Class<?> beanClass, String propertyName) {
		PropertyDescriptor propertyDescriptor = new PropertyDescriptorImpl(beanClass, propertyName);
        Method propertyGetter = propertyDescriptor.getPropertyGetter();
        if (propertyGetter.getAnnotation(UserChoice.class) != null) {
        	if (List.class.isAssignableFrom(propertyGetter.getReturnType()) 
        			&& EditableUtils.getElementClass(propertyGetter.getGenericReturnType()) == Long.class) {
        		return new PropertyContext<List<Long>>(propertyDescriptor) {

					@Override
					public Component renderForView(String componentId, IModel<List<Long>> model) {
				        List<Long> userIds = model.getObject();
				        if (userIds != null && !userIds.isEmpty()) {
				        	Dao dao = Gitop.getInstance(Dao.class);
				        	List<String> userNames = new ArrayList<>();
				        	for (Long userId: userIds) {
				        		userNames.add(dao.load(User.class, userId).getDisplayName());
				        	}
				            return new Label(componentId, StringUtils.join(userNames, ", " ));
				        } else {
							return new NotDefinedLabel(componentId);
				        }
					}

					@Override
					public PropertyEditor<List<Long>> renderForEdit(String componentId, IModel<List<Long>> model) {
						return new UserMultiChoiceEditor(componentId, this, model);
					}
        			
        		};
        	} else if (propertyGetter.getReturnType() == Long.class) {
        		return new PropertyContext<Long>(propertyDescriptor) {

					@Override
					public Component renderForView(String componentId, IModel<Long> model) {
				        Long userId = model.getObject();
				        if (userId != null) {
				        	User user = Gitop.getInstance(Dao.class).load(User.class, userId);
				            return new Label(componentId, user.getDisplayName());
				        } else {
							return new NotDefinedLabel(componentId);
				        }
					}

					@Override
					public PropertyEditor<Long> renderForEdit(String componentId, IModel<Long> model) {
						return new UserSingleChoiceEditor(componentId, this, model);
					}
        			
        		};
        	} else {
        		throw new RuntimeException("Annotation 'UserChoice' should be applied to property with type 'Long' or 'List<Long>'.");
        	}
        } else {
            return null;
        }
	}

}
