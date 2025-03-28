INSERT INTO public.prompt_types (name,created_at,description,updated_at,criteria) VALUES
	 ('main',now(),'Основная категория',now(),'если запрос касается возможностей ИИ помощника, его функционала, задач, организации информации и помощи в повседневных делах'),
	 ('financial_advisor',now(),'Финансовый советник',now(),'если запрос связан с инвестициями, фондовым рынком, анализом рынков, прогнозами, экономическими стратегиями и управлением финансами'),
	 ('general_communication',now(),'Обычная коммуникация',now(),'если сообщение не относится к другим категориям и представляет собой личное общение, неформальный диалог или другие вопросы'),
	 ('anxiety_help',now(),'Помощь при тревоге',now(),'если сообщение содержит признаки тревожности, беспокойства, стресса или просьбы о поддержке в сложной эмоциональной ситуации'),
	 ('category_auto_definition',now(),'Автоматическое определение категории',now(),'');
